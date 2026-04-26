import io.github.takenoko4096.mojangson.MojangsonParser;
import io.github.takenoko4096.mojangson.MojangsonSerializer;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;

void main() {
    final MojangsonParser parser = new MojangsonParser();

    final MojangsonCompound compound = parser.parse(
        """
        {
            id: uuid("0-0-0-0-0")
        }
        """,
        MojangsonCompound.class
    );

    final String string = MojangsonSerializer.serialize(compound);

    IO.println(string);
}
