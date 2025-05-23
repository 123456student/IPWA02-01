package org.ghostnets;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.io.Serializable;

@Entity
public class Recoverer implements Serializable
{
    @Id
    @GeneratedValue
    private long id;

    private String firstName;

    private String lastName;

    private String mailAddress;

    public Recoverer() {}

    public Recoverer(String firstName, String lastName)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    public Recoverer(String firstName, String lastName, String mailAddress)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setLastName(mailAddress);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMailAddress() { return mailAddress; }

    public void setMailAddress(String mailAddress) { this.mailAddress = mailAddress; }

    public long getId() {return this.id;}
}
