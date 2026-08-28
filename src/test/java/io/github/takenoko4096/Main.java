import io.github.takenoko4096.mojangson.*;
import io.github.takenoko4096.mojangson.values.*;
import org.junit.jupiter.api.Test;

@Test
void main() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    // Maven Central に publish するのはもう少しテストした後

    final MojangsonCompound compound = MojangsonParser.compound(
        """
        {
            int_array: [I; 0, 1, 2, 3],
            three: 3
        }
        """
    );

    final MojangsonIntArray array = compound.getOrThrow(MojangsonPath.of("int_array"), MojangsonValueTypes.INT_ARRAY);
    System.out.println(array);

    compound.getOrThrow("three", MojangsonValueTypes.INT).getValue();

    System.out.println(compound);

    // TODO: IntArray[int] 等の参照を制限すべき？
    // または MojangsonUnit<Integer> 等を作るか
}
