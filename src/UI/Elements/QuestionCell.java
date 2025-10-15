package UI.Elements;

import Controllers.QuestionDisplayViewModel;

import Models.Question;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class QuestionCell extends ListCell<QuestionDisplayViewModel> {
	private final VBox layout = new VBox();

	private final Label titleLabel = new Label();
	private final Label textLabel = new Label();
	private final Label statusLabel = new Label();
	private final Label permissionLabel = new Label(); // This is for illustrative purposes
	private final HBox actionsContainer = new HBox();

	private final VBox answers = new VBox();

	public QuestionCell() {
		titleLabel.setWrapText(true);
		textLabel.setWrapText(true);
		permissionLabel.setWrapText(true);

		layout.getChildren().addAll(titleLabel, actionsContainer, textLabel, statusLabel, permissionLabel, answers);
		layout.setFillWidth(true);
		layout.setMaxWidth(Double.MAX_VALUE);

		// Attach stylesheet
		layout.getStylesheets().add(getClass().getResource("/Styles/styles.css").toExternalForm());

		// Assign style classes
		layout.getStyleClass().add("question-cell");
		titleLabel.getStyleClass().add("question-title");
		textLabel.getStyleClass().add("question-body");
		statusLabel.getStyleClass().add("question-status");
	}

	@Override
	protected void updateItem(QuestionDisplayViewModel vm, boolean empty) {
		super.updateItem(vm, empty);
		if (empty || vm.getQuestion() == null) {
			setGraphic(null);
		} else {
			titleLabel.setText(vm.getQuestion().getTitle());
			textLabel.setText(truncate(vm.getQuestion().getText(), 150));
			statusLabel.setText(formatStatus(vm.getQuestion()));
			permissionLabel.setText(String.join(", ", vm.getPermissionList()));

			VBox answerContainer;
			Label answerTextLabel;
			Label authorNameLabel;
			Label permissionsLabel;

			for (int i = 0; i < vm.getAnswersForQuestion().size(); i++) {

				answerContainer = new VBox();
				authorNameLabel = new Label(vm.getAnswersForQuestion().get(i).getAuthorName() + ":");
				permissionsLabel = new Label(String.join(", ", vm.getAnswerUserPermissions().get(i)));

				Label userAnswerPermissionsLabel = new Label();
				
				answerContainer.getChildren().add(userAnswerPermissionsLabel);

				authorNameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

				answerTextLabel = new Label(vm.getAnswersForQuestion().get(i).getText());

				int leftPadding = (vm.getAnswersForQuestion().get(i).getReplyDepth() + 1) * 20;
				answerContainer.getChildren().addAll(authorNameLabel, answerTextLabel, permissionsLabel);
				answerContainer.setPadding(new Insets(10, 10, 10, leftPadding));

				answers.getChildren().add(answerContainer);

			}

			actionsContainer.getChildren().setAll(new HBox());

			for (String permission : vm.getPermissionList()) {
				switch (permission) {
				case "DELETE":
					Button deleteButton = new Button("Delete");
					deleteButton.setOnAction(_ -> {
						System.err.println("pressed");
						vm.deleteQuestion();
					});
					
					actionsContainer.getChildren().add(deleteButton);
					break;
				case "COMMENT":
					actionsContainer.getChildren().add(new Button("Comment"));
					break;
				case "EDIT":
					Button updateButton = new Button("Update");
					updateButton.setOnAction(_ -> {
						System.err.println("pressed");
						vm.updateQuestion();
					});
					
					actionsContainer.getChildren().add(updateButton);
					break;
				}
			}

			setGraphic(layout);
		}
	}

	private String truncate(String text, int limit) {
		return text.length() <= limit ? text : text.substring(0, limit) + "...";
	}

	private String formatStatus(Question q) {
		var parts = new java.util.ArrayList<String>();
		if (q.getAnsweredStatus())
			parts.add("Answered");
		if (q.getResolvedStatus())
			parts.add("Resolved");
		parts.add("Upvotes: " + q.getUpvoteCount());
		return String.join(" • ", parts);
	}
}
