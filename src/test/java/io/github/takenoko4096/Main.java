import io.github.takenoko4096.mojangson.MojangsonParser;
import io.github.takenoko4096.mojangson.MojangsonPath;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;

void main() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    var s2 = new MojangsonParser(true, true).parse(
        """
        {
            key: {
                array: [
                    {
                        foo: [bar, baz, us]
                    }
                ]
            }
        }
        """,
        MojangsonCompound.class
    );

    System.out.println(s2.get(MojangsonPath.of("key.array[{\"foo\":[\"baz\"]}].foo[2]"), MojangsonValueTypes.STRING));
}
