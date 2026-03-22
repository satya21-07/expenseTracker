//AuthRequest is a DTO used to receive login credentials (email and password) from the frontend when a user tries to authenticate.

package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //This automatically generates:getters,setters,toString(),equals(),hashCode()
@Builder
@AllArgsConstructor
@NoArgsConstructor //This creates a default empty constructor.
public class AuthRequest {
    private String email;
    private String password;
}
