import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.nbt.NbtFile;

void main() {
    IO.println("Hello, World!");

    final var file = new NbtFile(Path.of("src/test/resources/level.dat"));
    if (!file.exists()) file.create();

    file.editAuto(compound -> {
        compound.set(MojangsonPath.of("Data.difficulty_settings.difficulty"), "hard");
        return compound;
    });

    System.out.println(file.readAuto());
}
