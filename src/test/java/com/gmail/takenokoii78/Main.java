import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.nbt.NbtFile;

void main() {
	IO.println("Hello, World!");

	final var file = new NbtFile(Path.of("src/test/resources/level.dat"));
	if (!file.exists()) file.create();

	file.editAuto(compound -> {
		compound.set(MojangsonPath.of("Data.difficulty_settings.difficulty"), "hard");
		IO.println(compound.get(MojangsonPath.of("Data.Version.Name"), MojangsonValueTypes.STRING));
		return compound;
	});

	IO.println(MojangsonSerializer.serialize(MojangsonParser.compound(
        """
            {
                key: bool(bool(true)),
                s: false,
                d: "false",
                c: 'true'
            }
            """
    )));
}
