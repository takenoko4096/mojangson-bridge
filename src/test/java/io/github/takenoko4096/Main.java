import io.github.takenoko4096.mojangson.*;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;

void main() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    // Maven Central に publish するのはもう少しテストした後

    final MojangsonCompound compound = MojangsonParser.compound(
        """
        {
            array: [I;0,1,2,3]
        }
        """
    );

    var a = compound.getOrThrow(MojangsonPath.of("array[3]"), MojangsonValueTypes.INT);
    System.out.println(a);
}
