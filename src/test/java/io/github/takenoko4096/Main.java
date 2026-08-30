import io.github.takenoko4096.mojangson.*;
import io.github.takenoko4096.mojangson.values.*;
import org.junit.jupiter.api.Test;

@Test
void main() throws MojangsonPathUnableToAccessException {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    // Maven Central に publish するのはもう少しテストした後

    final MojangsonCompound compound = MojangsonParser.compound(
        """
        {
            int_array: [I; 0, 1, 2, 3]
        }
        """
    );

    compound.set(MojangsonPath.of("int_array[0]"), 10);
    System.out.println(compound);

    // TODO: IntArray[int] 等の参照を制限すべき？
    // または MojangsonUnit<Integer> 等を作るか
}
