import io.github.takenoko4096.json.JsonParser;
import io.github.takenoko4096.json.JsonPath;
import io.github.takenoko4096.json.JsonValueTypes;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.mojangson.MojangsonParser;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;

void main() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    final JsonObject o = JsonParser.object("""
        {
            "id": "minecraft:carrot_on_a_stick",
            "count": 1,
            "components": {
                "minecraft:custom_data": {
                    "arrays": {
                        "ints": [0, 1, 2, 3],
                        "list": [
                            {
                                "foo": "bar"
                            },
                            {
                                "one": 1
                            }
                        ]
                    }
                }
            }
        }
        """);

    System.out.println(o.getOrThrow(JsonPath.of("components.minecraft:custom_data.arrays.list[{\"one\": 1}].one"), JsonValueTypes.NUMBER));

    final MojangsonCompound o2 = MojangsonParser.compound("""
        {
            "id": "minecraft:carrot_on_a_stick",
            "count": 1,
            "components": {
                "minecraft:custom_data": {
                    "arrays": {
                        "ints": [0, 1, 2, 3],
                        "list": [
                            {
                                "foo": "bar"
                            },
                            {
                                "one": 255ub
                            }
                        ]
                    }
                }
            }
        }
        """);

    System.out.println(o2);
}
