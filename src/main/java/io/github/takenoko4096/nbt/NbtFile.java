package io.github.takenoko4096.nbt;

import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * nbt形式のファイルを表現します。
 */
@NullMarked
public class NbtFile {
    private final File file;

    /**
     * FileからNbtFileを作成します。
     * @param file Fileオブジェクト。
     */
    public NbtFile(File file) {
        this.file = file;

        if (file.exists() && !file.isFile()) {
            throw new IllegalArgumentException("パス '" + file + "' はディレクトリであり、ファイルパスとして無効です");
        }
    }

    /**
     * PathからNbtFileを作成します。
     * @param path Pathオブジェクト。
     */
    public NbtFile(Path path) {
        this(path.toFile());
    }

    /**
     * パスを表現するStringからNbtFileを作成します。
     * @param path パスとなる文字列。Path.of()でパースして使用されます。
     */
    public NbtFile(String path) {
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
            throw new IllegalStateException("ファイル '" + file + "' の削除に失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    /**
     * 圧縮されていない形式のファイルとしてバイナリを読み取ります。
     * @return デシリアライズ結果のコンパウンド。
     * @throws NbtReadException デコードに失敗した場合。
     */
    public MojangsonCompound readAsRaw() throws NbtReadException {
        return NbtDecoder.raw(file);
    }

    /**
     * データを圧縮されていない形式のバイナリに変換して書き込みます。
     * @param compound 書き込むデータのルートコンパウンド。
     * @throws NbtWriteException エンコードに失敗した場合。
     */
    public void writeAsRaw(MojangsonCompound compound) throws NbtWriteException {
        NbtEncoder.raw(file, compound);
    }

    /**
     * 圧縮されていない形式のファイルとしてバイナリを編集します。
     * @param function 構造を編集する関数。
     * @throws NbtReadException デコードに失敗した場合。
     * @throws NbtWriteException エンコードに失敗した場合。
     */
    public void editAsRaw(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        writeAsRaw(function.apply(readAsRaw()));
    }

    /**
     * GZip圧縮された形式のファイルとしてバイナリを読み取ります。
     * @return デシリアライズ結果のコンパウンド。
     * @throws NbtReadException デコードに失敗した場合。
     */
    public MojangsonCompound readAsCompressed() throws NbtReadException {
        return NbtDecoder.decompress(file);
    }

    /**
     * データをGZip圧縮された形式のバイナリに変換して書き込みます。
     * @param compound 書き込むデータのルートコンパウンド。
     * @throws NbtWriteException エンコードに失敗した場合。
     */
    public void writeAsCompressed(MojangsonCompound compound) throws NbtWriteException {
        NbtEncoder.compress(file, compound);
    }

    /**
     * GZip圧縮された形式のファイルとしてバイナリを編集します。
     * @param function 構造を編集する関数。
     * @throws NbtReadException デコードに失敗した場合。
     * @throws NbtWriteException エンコードに失敗した場合。
     */
    public void editAsCompressed(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        writeAsCompressed(function.apply(readAsCompressed()));
    }

    /**
     * GZip圧縮された形式のファイルであるかどうかを返します。
     * @return GZip圧縮されているならば、真。バイト列が短すぎる場合例外を投げます。
     * @throws NbtReadException デコードに失敗した場合、またはバイト列が短すぎて圧縮形式を判別できない場合。
     */
    public boolean isCompressed() throws NbtReadException {
        return NbtDecoder.isCompressed(file);
    }

    /**
     * GZip圧縮されているかどうかを調べ、適切な形式でバイナリをデコードして読み取ります。
     * @return デシリアライズ結果のコンパウンド。
     * @throws NbtReadException デコードに失敗した場合、またはバイト列が短すぎて圧縮形式を判別できない場合。
     */
    public MojangsonCompound readAuto() throws NbtReadException {
        if (isCompressed()) {
            return readAsCompressed();
        }
        else {
            return readAsRaw();
        }
    }

    /**
     * GZip圧縮されているかどうかを調べ、適切な形式でバイナリをデコードして編集を行い、再度適切な形式でエンコードします。
     * @param function 構造を編集する関数。
     * @throws NbtReadException デコードに失敗した場合、またはバイト列が短すぎて圧縮形式を判別できない場合。
     * @throws NbtWriteException エンコードに失敗した場合。
     */
    public void editAuto(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        if (isCompressed()) {
            editAsCompressed(function);
        }
        else {
            editAsRaw(function);
        }
    }
}
