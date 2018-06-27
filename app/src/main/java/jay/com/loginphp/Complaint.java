package jay.com.loginphp;

public class Complaint {
    String email;
    String location;
    String image;
    String created_at;
    String problem;
    String id;

    public Complaint(String email, String location, String image, String created_at, String id, String problem) {
        this.email = email;
        this.location = location;
        this.image = image;
        this.created_at = created_at;
        this.id = id;
        this.problem = problem;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getId() {
        return id;
    }

    public String getProblem() {
        return problem;
    }
}
