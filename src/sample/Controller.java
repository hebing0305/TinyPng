package sample;

import com.sun.deploy.Environment;
import com.tinify.Source;
import com.tinify.Tinify;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;

public class Controller {
    @FXML
    private TextField myTextField;
    @FXML
    private Label msg;
    Thread thread;

    public void compressImage(ActionEvent actionEvent) throws IOException {
        if (thread != null) {
            thread.stop();
        }

        Tinify.setKey(myTextField.getText());
        boolean isValidate=false;
        try {
            isValidate=Tinify.validate();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!isValidate || Tinify.compressionCount()<=0){
            showMsg("api_key无效！或者使用次数为0！");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择需要压缩的图片：");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("*.jpg", "*.png");
        fileChooser.setSelectedExtensionFilter(filter);
        List<File> fileList = fileChooser.showOpenMultipleDialog(null);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择图片保存的路径：");
        File savePath = directoryChooser.showDialog(null);
        if (fileList != null && savePath != null) {
            thread = new Thread(() -> {
                int error=0;
                for (File file : fileList) {
                    try {
                        showMsg("正在压缩图片:"+file.getName());
                        Source source=Tinify.fromFile(file.getAbsolutePath());
                        source.toFile(new File(savePath, file.getName()).getAbsolutePath());
                    } catch (Exception e) {
                        error++;
                        e.printStackTrace();
                    }
                }
                showMsg("压缩操作已经执行完成！错误次数："+error);
            });
            thread.start();
        }
    }

    public void showMsg(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                msg.setText(text);
            }
        });
    }

}
