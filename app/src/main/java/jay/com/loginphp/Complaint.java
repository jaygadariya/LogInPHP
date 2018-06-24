package jay.com.loginphp;

public class Complaint {
    String email;
    String location;
    String image;
    String created_at;

    public Complaint(String email, String location, String image, String created_at) {
        this.email = email;
        this.location = location;
        this.image = image;
        this.created_at = created_at;
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
}
