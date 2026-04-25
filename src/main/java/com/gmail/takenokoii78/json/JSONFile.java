package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Function;

@NullMarked
public class JSONFile {
    private final File file;

    public JSONFile(File file) {
        this.file = file;

        if (file.exists() && !file.isFile()) {
            throw new IllegalArgumentException("パス '" + file + "' はディレクトリであり、ファイルパスとして無効です");
        }
    }

    public JSONFile(Path path) {
        this(path.toFile());
    }

    public JSONFile(String path) {
        this(Path.of(path));
    }

    public File getFile() {
        return file;
    }

    protected String readAsString() throws IllegalStateException {
        if (exists()) try {
            return String.join("\n", Files.readAllLines(file.toPath()));
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' の読み取りに失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    protected void writeAsString(String json) throws IllegalStateException {
        if (exists()) try {
            Files.write(
                file.toPath(),
                Arrays.asList(json.split("\\n")),
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' への書き込みに失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    public boolean exists() {
        return file.exists();
    }

    public void create() throws IllegalStateException {
        if (exists()) throw new IllegalStateException("既にファイル '" + file + "' は存在します");
        else try {
            Files.createFile(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' の作成に失敗しました", e);
        }
    }

    public void delete() throws IllegalStateException {
        if (exists()) try {
            Files.delete(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' の削除に失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    public long getSize() throws IllegalStateException {
        if (exists()) try {
            return Files.size(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' のサイズの取得に失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    public JSONStructure read() throws JSONParseException, IllegalStateException {
        return JSONParser.structure(readAsString());
    }

    public void write(JSONStructure structure) throws JSONSerializationException, IllegalStateException {
        writeAsString(JSONSerializer.serialize(structure));
    }

    public JSONObject readAsObject() throws JSONParseException, IllegalStateException {
        return JSONParser.object(readAsString());
    }

    public JSONArray readAsArray() throws JSONParseException, IllegalStateException {
        return JSONParser.array(readAsString());
    }

    public void edit(Function<JSONStructure, JSONStructure> function) throws JSONParseException, JSONSerializationException, IllegalStateException {
        write(function.apply(read()));
    }
}
