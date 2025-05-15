package ru.gr362;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class Dialogs {
    public static File showFileDialog(boolean save){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(save ? "Сохранить в файл" : "Загрузить из файла");
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(!save);
        var jpgFilter = new FileNameExtensionFilter("Файлы формата jpeg", "jpg", "jpeg");
        var fracFilter = new FileNameExtensionFilter("Файла собственного формата frac", "frac");
        chooser.setFileFilter(jpgFilter);
        chooser.setFileFilter(fracFilter);
        if ((save ?
                chooser.showSaveDialog(null) :
                chooser.showOpenDialog(null))
                == JFileChooser.APPROVE_OPTION){
            var fileName = chooser.getSelectedFile().getAbsolutePath();
            var filter = chooser.getFileFilter();
            if (!filter.accept(new File(fileName))){
                if (filter == jpgFilter) fileName += ".jpg";
                if (filter == fracFilter) fileName += ".frac";
            }
            return new File(fileName);
        }
        return null;
    }
}