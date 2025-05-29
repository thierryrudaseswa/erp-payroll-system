package rw.gov.erp.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rw.gov.erp.entity.Employee;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {
    private final String code;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public static UserPrincipal create(Employee employee) {
        Collection<GrantedAuthority> authorities = employee.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                employee.getCode(),
                employee.getEmail(),
                employee.getPassword(),
                authorities,
                employee.getStatus() == Employee.EmployeeStatus.ACTIVE
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 