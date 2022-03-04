package ru.chpetr;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    static final String SAVE_SLOT_1 = "E://Games/savegames/save1.dat";
    static final String SAVE_SLOT_2 = "E://Games/savegames/save2.dat";
    static final String SAVE_SLOT_3 = "E://Games/savegames/save3.dat";
    static final String ZIP_SAVES = "E://Games/savegames/zipSaves.zip";
    static final String PATH_EXTRACT = "E://Games/savegames/";

    public static void main(String[] args) {
	    GameProgress level1 = new GameProgress(100, 1, 1, 0);
	    GameProgress level5 = new GameProgress(54, 3, 6, 15);
	    GameProgress level13 = new GameProgress(88, 2, 21, 44);

        File save1 = saveGame(SAVE_SLOT_1, level1);
        File save2 = saveGame(SAVE_SLOT_2, level5);
        File save3 = saveGame(SAVE_SLOT_3, level13);
        List<File> listSaves = Arrays.asList(save1, save2, save3);

        zipFiles(ZIP_SAVES, listSaves);

        openZip(ZIP_SAVES, PATH_EXTRACT);

        System.out.println(openProgress(SAVE_SLOT_2));
    }

    static File saveGame (String name, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(name);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
            return new File(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    static void zipFiles (String pathZip, List<File> pathsFiles) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(pathZip))) {
            for (File pathFile : pathsFiles) {
                try (FileInputStream fis = new FileInputStream(pathFile)) {
                    ZipEntry entry = new ZipEntry(pathFile.getName());
                    zos.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zos.write(buffer);
                    zos.closeEntry();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            for (File pathFile : pathsFiles) {
                pathFile.delete();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void openZip(String pathFile, String pathExtract) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(pathFile))) {
            ZipEntry entry;
            String name;
            File path = new File(pathExtract);
            if (!path.exists()) {
                path.mkdir();
            }

            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(pathExtract + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static GameProgress openProgress(String pathFile) {
        try (FileInputStream fis = new FileInputStream(pathFile);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (GameProgress) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
