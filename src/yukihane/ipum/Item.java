package yukihane.ipum;


public class Item {

    private String name, comment;

    public Item(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public void setName(String str) {
        name = str;
    }

    public void setComment(String str) {
        comment = str;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}
