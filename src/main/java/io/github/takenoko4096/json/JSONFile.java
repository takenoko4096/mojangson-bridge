package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JSONArray;
import io.github.takenoko4096.json.values.JSONObject;
import io.github.takenoko4096.json.values.JSONStructure;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Function;

/**
 * json形式のファイルを表現します。
 */
@NullMarked
public class JSONFile {
    private final File file;

    /**
     * FileからJSONFileを作成します。
     * @param file Fileオブジェクト。
     */
    public JSONFile(File file) {
        this.file = file;

        if (file.exists() && !file.isFile()) {
            throw new IllegalArgumentException("パス '" + file + "' はディレクトリであり、ファイルパスとして無効です");
        }
    }

    /**
     * PathからJSONFileを作成します。
     * @param path Pathオブジェクト。
     */
    public JSONFile(Path path) {
        this(path.toFile());
    }

    /**
     * パスを表現するStringからJSONFileを作成します。
     * @param path パスとなる文字列。Path.of()でパースして使用されます。
     */
    public JSONFile(String path) {
        this(Path.of(path));
    }

    /**
     * java.io.Fileとして取得します。
     * @return ラップされていたFileオブジェクト。
     */
    public File toFile() {
        return file;
    }

    /**
     * ファイルの内容を文字列として読み取り、文字列として返します。
     * @return 空白文字・改行を含む文字列。
     * @throws IllegalStateException ファイルが存在しない、またはI/O例外の場合。
     */
    protected String readAsString() throws IllegalStateException {
        if (exists()) try {
            return String.join("\n", Files.readAllLines(file.toPath()));
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' の読み取りに失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    /**
     * ファイルの内容を引数に渡された文字列で上書きします。
     * @param json 空白文字・改行を含む文字列。
     * @throws IllegalStateException ファイルが存在しない、またはI/O例外の場合。
     */
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

    /**
     * ファイルが存在するかどうかを返します。
     * @return 存在する場合に真。
     */
    public boolean exists() {
        return file.exists();
    }

    /**
     * 空のファイルを作成します。
     * @throws IllegalStateException 既に存在する、またはI/O例外の場合。
     */
    public void create() throws IllegalStateException {
        if (exists()) throw new IllegalStateException("既にファイル '" + file + "' は存在します");
        else try {
            Files.createFile(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' の作成に失敗しました", e);
        }
    }

    /**
     * ファイルを削除します。
     * @throws IllegalStateException ファイルが存在しない、またはI/O例外の場合。
     */
    public void delete() throws IllegalStateException {
        if (exists()) try {
            Files.delete(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' の削除に失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    /**
     * ファイルサイズを取得します。
     * @return ファイルサイズ (bytes)。
     * @throws IllegalStateException ファイルが存在しない、またはI/O例外の場合。
     */
    public long size() throws IllegalStateException {
        if (exists()) try {
            return Files.size(file.toPath());
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイル '" + file + "' のサイズの取得に失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    /**
     * json構造としてファイルの記述を読み取ります。
     * @return パース結果。
     * @throws JSONParseException パースに失敗した場合。
     * @throws IllegalStateException ファイルの読み取りに失敗した場合。
     */
    public JSONStructure read() throws JSONParseException, IllegalStateException {
        return JSONParser.structure(readAsString());
    }

    /**
     * json構造をシリアライズして書き込みます。
     * @param structure json構造。
     * @throws JSONSerializationException シリアライズに失敗した場合。
     * @throws IllegalStateException ファイルの書き込みに失敗した場合。
     */
    public void write(JSONStructure structure) throws JSONSerializationException, IllegalStateException {
        writeAsString(JSONSerializer.structure(structure));
    }

    /**
     * ルートがオブジェクトであることを期待してファイルの記述を読み取ります。
     * @return jsonオブジェクト。
     * @throws JSONParseException パースに失敗した場合。
     * @throws IllegalStateException ファイルの読み取りに失敗した場合。
     */
    public JSONObject readAsObject() throws JSONParseException, IllegalStateException {
        return JSONParser.object(readAsString());
    }

    /**
     * ルートが配列であることを期待してファイルの記述を読み取ります。
     * @return json配列。
     * @throws JSONParseException パースに失敗した場合。
     * @throws IllegalStateException ファイルの読み取りに失敗した場合。
     */
    public JSONArray readAsArray() throws JSONParseException, IllegalStateException {
        return JSONParser.array(readAsString());
    }

    /**
     * jsonファイルの記述を読み取り、任意の関数によって構造を編集して再度書き込みます。
     * @param function 構造を編集する関数。
     * @throws JSONParseException パースに失敗した場合。
     * @throws JSONSerializationException シリアライズに失敗した場合。
     * @throws IllegalStateException ファイルの読み取りまたは書き込みに失敗した場合。
     */
    public void edit(Function<JSONStructure, JSONStructure> function) throws JSONParseException, JSONSerializationException, IllegalStateException {
        write(function.apply(read()));
    }
}
