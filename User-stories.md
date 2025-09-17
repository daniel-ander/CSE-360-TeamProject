# ✅ User Stories – Checklist



## New User Tasks
- [ ] First user creates account (username, password, info) via special screen.  
- [ ] First user automatically assigned **admin role**.  
- [ ] First user must log in again after setup.  
- [ ] Users can be assigned **one or more roles** (admin, student, instructor, staff, reviewer).  
- [ ] If user has only one role → system directs them to that role’s home screen after login.  
- [ ] If user has multiple roles → system prompts them to select which role to use.  
- [ ] User can log out at any time or switch roles.  

## Admin Tasks
- [ ] Admin can invite users with a **one-time code** and **expiration date**.  
- [ ] Admin can reset a user’s password with a **one-time password**.  
  - [ ] User must log in with one-time password.  
  - [ ] User must create a new password.  
  - [ ] User must log in again after creating the new password.  
- [ ] Admin can delete a user account.  
  - [ ] System asks: "Are you sure?" → must answer **Yes**.  
  - [ ] Admin cannot delete their own account.  
  - [ ] At least one admin account must always remain.  
- [ ] Admin can list all user accounts.  
  - [ ] Display includes username, name, email, and roles.  
- [ ] Admin can add or remove user roles.  
  - [ ] Admin cannot remove their own admin role.  