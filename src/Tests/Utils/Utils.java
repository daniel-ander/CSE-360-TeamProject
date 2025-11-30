package Tests.Utils;

import Models.DBModels.UserDTO;
import Models.Interfaces.ISharedUser.Role;

public class Utils {
	public static UserDTO getUserDTOModel() {
    	return new UserDTO(1, "username", Role.student);
    }
}