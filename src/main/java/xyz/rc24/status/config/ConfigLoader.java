package xyz.rc24.status.config;

import org.yaml.snakeyaml.Yaml;
import xyz.rc24.status.StatusApp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static xyz.rc24.status.StatusApp.LOGGER;

public class ConfigLoader
{
    public static Config load()
    {
        File parent = new File("data");
        File file = new File(parent, "config.yml");
        Path path = file.toPath();

        if(!(file.exists()))
        {
            try(InputStream is = StatusApp.class.getResourceAsStream("/config.yml"))
            {
                if(is == null)
                    throw new IOException("config.yml file is missing from the JAR!");

                Files.createDirectories(parent.toPath());
                try(FileChannel fileChannel = FileChannel.open(path, CREATE_NEW, WRITE))
                {fileChannel.write(ByteBuffer.wrap(is.readAllBytes()));}
            }
            catch(Exception e) {throw new RuntimeException("Failed to copy default config.yml file:", e);}

            LOGGER.info("The config file has been created. Populate it with the correct options.");
            System.exit(1);
        }

        try(FileReader reader = new FileReader(file)) {return new Yaml().loadAs(reader, Config.class);}
        catch(Exception e) {throw new RuntimeException("Failed to load config.yml file:", e);}
    }
}
