import io.github.takenoko4096.mojangson.MojangsonParser;
import io.github.takenoko4096.mojangson.values.MojangsonInt;
import io.github.takenoko4096.mojangson.values.MojangsonIntArray;
import io.github.takenoko4096.mojangson.values.TypedMojangsonList;

void main() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    // Maven Central に publish するのはもう少しテストした後

    final MojangsonIntArray ints = MojangsonParser.intArray("[I; 0, 1, 2, 3]");
    final TypedMojangsonList<MojangsonInt> view1 = ints.listView();
    final TypedMojangsonList<MojangsonInt> view2 = ints.listView();

    view1.set(0, 10);
    System.out.println(view1.delete(-1));
    System.out.println(view1.getOrThrow(0));
    System.out.println(ints);

    System.out.println(view1);
    System.out.println(ints.listView());
}
