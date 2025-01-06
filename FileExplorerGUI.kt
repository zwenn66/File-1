import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileExplorerGUI extends JFrame {
    private JTextField directoryTextField;
    private JTextField fileNameTextField;
    private JTextArea resultTextArea;
    private String copiedFilePath; // 保存拷贝的文件路径

    public FileExplorerGUI() {
        setTitle("文件浏览器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel directoryLabel = new JLabel("目录:");
        directoryTextField = new JTextField();
        JLabel fileNameLabel = new JLabel("文件名:");
        fileNameTextField = new JTextField();
        inputPanel.add(directoryLabel);
        inputPanel.add(directoryTextField);
        inputPanel.add(fileNameLabel);
        inputPanel.add(fileNameTextField);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        JButton searchButton = new JButton("搜索");
        JButton copyButton = new JButton("拷贝");
        JButton pasteButton = new JButton("粘贴");
        JButton renameButton = new JButton("重命名");
        JButton countButton = new JButton("统计");
        JButton previewButton = new JButton("预览");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(pasteButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(countButton);
        buttonPanel.add(previewButton);

        // 使用Lambda表达式为按钮添加ActionListener
        searchButton.addActionListener(e -> searchFiles());
        copyButton.addActionListener(e -> copyFile());
        pasteButton.addActionListener(e -> pasteFile());
        renameButton.addActionListener(e -> renameFile());
        countButton.addActionListener(e -> countFiles());
        previewButton.addActionListener(e -> previewFile());

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void searchFiles() {
        String directory = directoryTextField.getText();
        String fileName = fileNameTextField.getText();
        List<String> result = FileSearch.searchFiles(directory, fileName);
        resultTextArea.setText(String.join("\n", result));
    }

    private void copyFile() {
        String directory = directoryTextField.getText();
        String fileName = fileNameTextField.getText();
        List<String> searchResult = FileSearch.searchFiles(directory, fileName);
        if (!searchResult.isEmpty()) {
            copiedFilePath = searchResult.get(0); // 保存第一个匹配的文件路径
            // 将文件路径复制到系统剪贴板
            StringSelection stringSelection = new StringSelection(copiedFilePath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null, "文件路径已复制到剪贴板：" + copiedFilePath);
        } else {
            JOptionPane.showMessageDialog(null, "未找到匹配的文件");
        }
    }

    private void pasteFile() {
        if (copiedFilePath == null || copiedFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(null, "剪贴板中没有文件路径");
            return;
        }

        // 提示用户输入目标路径
        String destinationPath = JOptionPane.showInputDialog("请输入目标文件路径：");
        if (destinationPath != null && !destinationPath.isEmpty()) {
            try {
                FileOperations.copyFile(copiedFilePath, destinationPath);
                JOptionPane.showMessageDialog(null, "文件粘贴成功：" + destinationPath);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "文件粘贴失败");
            }
        }
    }

    private void renameFile() {
        String directory = directoryTextField.getText();
        String fileName = fileNameTextField.getText();
        List<String> searchResult = FileSearch.searchFiles(directory, fileName);
        if (!searchResult.isEmpty()) {
            String filePath = searchResult.get(0);
            // 提示用户输入新文件名
            String newName = JOptionPane.showInputDialog("请输入新文件名：");
            if (newName != null && !newName.isEmpty()) {
                FileOperations.renameFile(filePath, newName);
                JOptionPane.showMessageDialog(null, "文件重命名成功");
                searchFiles(); // 刷新搜索结果
            }
        } else {
            JOptionPane.showMessageDialog(null, "未找到匹配的文件");
        }
    }

    private void countFiles() {
        String directory = directoryTextField.getText();
        int fileCount = FileSearch.countFilesInFolder(directory);
        JOptionPane.showMessageDialog(null, "文件夹中的文件数量：" + fileCount);
    }

    private void previewFile() {
        String directory = directoryTextField.getText();
        String fileName = fileNameTextField.getText();
        List<String> searchResult = FileSearch.searchFiles(directory, fileName);
        if (!searchResult.isEmpty()) {
            String filePath = searchResult.get(0);
            try {
                String previewContent = FileOperations.previewTextFile(filePath, 100);
                showPreviewWindow(previewContent);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "文件预览失败");
            }
        } else {
            JOptionPane.showMessageDialog(null, "未找到匹配的文件");
        }
    }

    private void showPreviewWindow(String content) {
        JTextArea previewTextArea = new JTextArea(content);
        previewTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(previewTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "文件预览", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileExplorerGUI fileExplorer = new FileExplorerGUI();
            fileExplorer.setVisible(true);
        });
    }
}
