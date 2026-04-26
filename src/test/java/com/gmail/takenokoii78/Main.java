import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONSerializer;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonInt;
import com.gmail.takenokoii78.mojangson.values.MojangsonString;
import com.gmail.takenokoii78.mojangson.values.MojangsonStructure;
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

    final MojangsonParser p = new MojangsonParser();
    p.register("strUns", (args) -> {
        return MojangsonString.valueOf(
            Integer.toUnsignedString(
                ((MojangsonInt) args.getFirst()).getValue()
            )
        );
    });

	IO.println(MojangsonSerializer.serialize((MojangsonStructure) p.parse(
        """
            {
                key: bool(bool(true)),
                s: false,
                d: "false",
                c: 'true',
                id: uuid('f81d4fae-7dec-11d0-a765-00a0c91e6bf6'),
                n: strUns(2147483649u),
                f: 9223372036854775808uL,
                g: 9223372036854775807L,
                i: 2147483648ui
            }
            """
    )));

    final var array = MojangsonParser.intArray(String.format("uuid('%s')", UUID.randomUUID()));
}
