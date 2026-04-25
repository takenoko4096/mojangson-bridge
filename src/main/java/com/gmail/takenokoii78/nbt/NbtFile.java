package com.gmail.takenokoii78.nbt;

import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

@NullMarked
public class NbtFile {
    private final File file;

    public NbtFile(File file) {
        this.file = file;

        if (file.exists() && !file.isFile()) {
            throw new IllegalArgumentException("パス '" + file + "' はディレクトリであり、ファイルパスとして無効です");
        }
    }

    public NbtFile(Path path) {
        this(path.toFile());
    }

    public NbtFile(String path) {
        this(Path.of(path));
    }

    public File getFile() {
        return file;
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
            throw new IllegalStateException("ファイル '" + file + "' の削除に失敗しました", e);
        }
        else throw new IllegalStateException("ファイル '" + file + "' が存在しません");
    }

    public MojangsonCompound readAsRaw() throws NbtReadException {
        return NbtReader.raw(file);
    }

    public void writeAsRaw(MojangsonCompound compound) throws NbtWriteException {
        NbtWriter.raw(file, compound);
    }

    public void editAsRaw(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        writeAsRaw(function.apply(readAsRaw()));
    }

    public MojangsonCompound readAsCompressed() throws NbtReadException {
        return NbtReader.decompress(file);
    }

    public void writeAsCompressed(MojangsonCompound compound) throws NbtWriteException {
        NbtWriter.compress(file, compound);
    }

    public void editAsCompressed(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        writeAsCompressed(function.apply(readAsCompressed()));
    }

    public boolean isCompressed() throws NbtReadException {
        return NbtReader.isCompressed(file);
    }

    public MojangsonCompound readAuto() throws NbtReadException {
        if (isCompressed()) {
            return readAsCompressed();
        }
        else {
            return readAsRaw();
        }
    }

    public void editAuto(Function<MojangsonCompound, MojangsonCompound> function) throws NbtReadException, NbtWriteException {
        if (isCompressed()) {
            editAsCompressed(function);
        }
        else {
            editAsRaw(function);
        }
    }
}
