package mako3.found.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class GroupMemberJson {

    private String name;

    private String email;

    private String userType;

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonSetter("user_type")
    public void setUserType(String userType) {
        this.userType = userType;
    }

}
