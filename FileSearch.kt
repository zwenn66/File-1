import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {
    public static List<String> searchFiles(String directory, String fileName) {
        List<String> resultList = new ArrayList<>();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    resultList.addAll(searchFiles(file.getAbsolutePath(), fileName));
                } else if (file.getName().contains(fileName)) {
                    resultList.add(file.getAbsolutePath());
                }
            }
        }
        return resultList;
    }

    public static int countFilesInFolder(String directory) {
        File folder = new File(directory);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                return files.length;
            }
        }
        return 0;
    }
}
