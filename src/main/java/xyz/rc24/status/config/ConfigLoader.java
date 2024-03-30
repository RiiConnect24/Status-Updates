/*
 * MIT License
 * Copyright (c) 2019-2021 RiiConnect24
 * Copyright (c) 2019-2024 Artuto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
