package jay.com.loginphp;

public class Complaint {
    String email;
    String location;
    String image;
    String created_at;
    String problem;
    String id;
    String status;
    String discription;

    public Complaint(String email, String location, String image, String created_at, String id, String problem,String status,String discription) {
        this.email = email;
        this.location = location;
        this.image = image;
        this.created_at = created_at;
        this.id = id;
        this.problem = problem;
        this.status = status;
        this.discription = discription;
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

    public String getStatus() {
        return status;
    }

    public String getDiscription() {
        return discription;
    }
}
