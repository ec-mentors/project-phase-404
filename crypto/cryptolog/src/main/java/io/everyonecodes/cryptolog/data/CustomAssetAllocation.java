package io.everyonecodes.cryptolog.data;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "custom_asset_allocation")
public class CustomAssetAllocation {

    @Id
    @GeneratedValue
    @Column(name = "custom_allocation_id")
    private Long customAllocationId;

    @NotEmpty
    @Column(name = "custom_allocation_name")
    private String customAllocationName;

    @Column(name = "invested_coins")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> investedCoins = new HashSet<>();

    @OneToOne
    private User user;

    public CustomAssetAllocation(Long customAllocationId, String allocationName) {
        this.customAllocationId = customAllocationId;
        this.customAllocationName = allocationName;
    }

    public CustomAssetAllocation(String allocationName) {
        this.customAllocationName = allocationName;
    }

    public CustomAssetAllocation() {

    }

    public Long getId() {
        return customAllocationId;
    }

    public void setId(Long id) {
        this.customAllocationId = id;
    }

    public String getAllocationName() {
        return customAllocationName;
    }

    public void setAllocationName(String allocationName) {
        this.customAllocationName = allocationName;
    }

    public Set<String> getInvestedCoins() {
        return investedCoins;
    }

    public void setInvestedCoins(Set<String> investedCoins) {
        this.investedCoins = investedCoins;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomAssetAllocation that = (CustomAssetAllocation) o;
        return Objects.equals(customAllocationId, that.customAllocationId) && Objects.equals(customAllocationName, that.customAllocationName) && Objects.equals(investedCoins,
                that.investedCoins) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customAllocationId, customAllocationName, investedCoins, user);
    }
}
