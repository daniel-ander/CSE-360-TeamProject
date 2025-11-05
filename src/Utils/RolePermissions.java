package Utils;

import java.util.Arrays;
import java.util.List;

import Models.User;

public class RolePermissions {
	// NOTE:- The reason there is one for question and answer each is for clarity
	// and for the reason that the permissions are likely to diverge
	public static enum QuestionPermissions {
		VIEW, COMMENT, DELETE, APPROVE, UPVOTE, EDIT;
		
		public static List<String> getAllPermissions() {
			return Arrays.stream(QuestionPermissions.values())
					.map(Enum::name)
					.toList();
		}
		
		public static List<String> getDefaultPermissionsForRole(User.Role role) {
			switch (role) {
			case admin:
				return List.of(VIEW.name(), COMMENT.name(), DELETE.name(), APPROVE.name(), EDIT.name(), UPVOTE.name());
			case student:
				return List.of(VIEW.name(), COMMENT.name(), UPVOTE.name());
			case instructor:
				return List.of(VIEW.name(), COMMENT.name(), EDIT.name(), APPROVE.name(), UPVOTE.name());
			case staff:
				return List.of(VIEW.name(), COMMENT.name(), UPVOTE.name(), EDIT.name());
			default:
				return List.of(VIEW.name(), COMMENT.name(), UPVOTE.name(), APPROVE.name());
			}
		}
	}
	
	public static enum AnswerPermissions {
		VIEW, COMMENT, DELETE, APPROVE, UPVOTE, EDIT;
		
		public static List<String> getAllPermissions() {
			return Arrays.stream(AnswerPermissions.values())
					.map(Enum::name)
					.toList();
		}
		
		public static List<String> getDefaultPermissionsForRole(User.Role role) {
			switch (role) {
			case admin:
				return List.of(VIEW.name(), COMMENT.name(), DELETE.name(), APPROVE.name(), EDIT.name(), UPVOTE.name());
			case student:
				return List.of(VIEW.name(), COMMENT.name(), UPVOTE.name());
			case instructor:
				return List.of(VIEW.name(), COMMENT.name(), EDIT.name(), APPROVE.name(), UPVOTE.name());
			case staff:
				return List.of(VIEW.name(), COMMENT.name(), UPVOTE.name(), EDIT.name());
			default:
				return List.of(VIEW.name(), COMMENT.name(), UPVOTE.name(), APPROVE.name());
			}
		}
	}
	

}