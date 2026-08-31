import io.github.takenoko4096.json.JsonParser;
import io.github.takenoko4096.json.JsonPath;
import io.github.takenoko4096.json.JsonValueTypes;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.mojangson.*;
import org.junit.jupiter.api.Test;

@Test
void main() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    // TODO: Maven Central に publish するのはもう少しテストした後

    final JsonObject object = JsonParser.object(
        """
        {
            "pack": {
                "description": "This is a description.",
                "min_format": [106, 1],
                "max_format": [109, 1]
            }
        }
        """
    );

    final JsonPath path = JsonPath.of("pack{\"min_format\": [1]}.max_format[0]");

    System.out.println(object.getOrThrow(path, JsonValueTypes.NUMBER));

    object.set(path, 108);

    System.out.println(object);
}
