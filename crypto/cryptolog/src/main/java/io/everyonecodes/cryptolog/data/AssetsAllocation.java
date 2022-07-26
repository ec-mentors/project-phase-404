package io.everyonecodes.cryptolog.data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Entity
@Table(name = "assets_allocation")
public class AssetsAllocation {

    @Id
    @GeneratedValue
    @Column(name = "allocation_id")
    private Long id;

    @NotEmpty
    @Column(name = "allocation_name")
    private String allocationName;

    public AssetsAllocation(Long id, String allocationName) {
        this.id = id;
        this.allocationName = allocationName;
    }

    public AssetsAllocation(String allocationName) {
        this.allocationName = allocationName;
    }
    public AssetsAllocation() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAllocationName() {
        return allocationName;
    }

    public void setAllocationName(String allocationName) {
        this.allocationName = allocationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetsAllocation that = (AssetsAllocation) o;
        return Objects.equals(id, that.id) && Objects.equals(allocationName, that.allocationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, allocationName);
    }
}
