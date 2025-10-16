package UI.Elements;

import Controllers.QuestionDisplayViewModel;

import Models.Question;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class QuestionCell extends ListCell<QuestionDisplayViewModel> {
	private final VBox layout = new VBox();

	private final Label titleLabel = new Label();
	private TextField titleTextField = new TextField();
	
	private final Label bodyTextLabel = new Label();
	private TextField bodyTextField = new TextField();
	
	private TextField newCommentTextField = new TextField();
	
	private final Label statusLabel = new Label();
	private final Label permissionLabel = new Label(); // This is for illustrative purposes
	private final HBox actionsContainer = new HBox();

	private final VBox answersVBox = new VBox();
	private final Button updateButton = new Button("Edit");

	public QuestionCell() {
		titleLabel.setWrapText(true);
		titleTextField.setVisible(false);
		bodyTextField.setVisible(false);
		
		StackPane titleStackPane = new StackPane(titleLabel, titleTextField);
		titleStackPane.setAlignment(Pos.CENTER_LEFT);
		
		StackPane bodyStackPane = new StackPane(bodyTextLabel, bodyTextField);
		bodyStackPane.setAlignment(Pos.CENTER_LEFT);
		
		bodyTextLabel.setWrapText(true);
		permissionLabel.setWrapText(true);
		
		newCommentTextField.setVisible(false);
		newCommentTextField.setManaged(false);
		
		layout.getChildren().addAll(titleStackPane, bodyStackPane, newCommentTextField, statusLabel, actionsContainer, answersVBox);
		layout.setFillWidth(true);
		layout.setMaxWidth(Double.MAX_VALUE);

		// Attach stylesheet
		layout.getStylesheets().add(getClass().getResource("/Styles/styles.css").toExternalForm());

		// Assign style classes
		layout.getStyleClass().add("question-cell");
		titleLabel.getStyleClass().add("question-title");
		bodyTextLabel.getStyleClass().add("question-body");
		statusLabel.getStyleClass().add("question-status");
	}

	@Override
	protected void updateItem(QuestionDisplayViewModel vm, boolean empty) {
		System.out.println("Updated");
		super.updateItem(vm, empty);
		if (empty || vm.getQuestion() == null) {
			setGraphic(null);
		} else {
			titleLabel.setText(vm.getQuestion().getTitle());
			bodyTextLabel.setText(truncate(vm.getQuestion().getText(), 150));
			statusLabel.setText(formatStatus(vm.getQuestion()));
			//permissionLabel.setText(String.join(", ", vm.getPermissionList()));

			VBox answerContainer;
			Label answerTextLabel;
			Label authorNameLabel;
			Label permissionsLabel;
			HBox answerActionContainer;

			for (int i = 0; i < vm.getAnswersForQuestion().size(); i++) {

				answerContainer = new VBox();
				
				authorNameLabel = new Label(vm.getAnswersForQuestion().get(i).getAuthorName() + ":");
				//for(List<String> answerPermissions : vm.getAnswerUserPermissions()) {
					answerActionContainer = new HBox();
					
					for(String answerPermission : vm.getAnswerUserPermissions().get(i)) {
						switch (answerPermission) {
						case "DELETE":
							Button deleteAnswerButton = new Button("Delete");
							deleteAnswerButton.setOnAction(_ -> {
								vm.deleteQuestion();
							});
							
							answerActionContainer.getChildren().add(deleteAnswerButton);
							break;
						case "COMMENT":
							Button commentButton = new Button("Comment");
							commentButton.setOnAction(_ -> {
								vm.addAnswer("This text");
							});
							
							answerActionContainer.getChildren().add(commentButton);
							break;
						case "EDIT":
							Button updateButton = new Button("Update");
							updateButton.setOnAction(_ -> {
								vm.updateQuestion("", "");
							});
							
							answerActionContainer.getChildren().add(updateButton);
							break;
						}
					}
				Label userAnswerPermissionsLabel = new Label();
				
				answerContainer.getChildren().add(userAnswerPermissionsLabel);

				authorNameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

				answerTextLabel = new Label(vm.getAnswersForQuestion().get(i).getText());

				int leftPadding = (vm.getAnswersForQuestion().get(i).getReplyDepth() + 1) * 20;
				answerContainer.getChildren().addAll(authorNameLabel, answerTextLabel, answerActionContainer);
				answerContainer.setPadding(new Insets(10, 10, 10, leftPadding));
				
				answersVBox.getChildren().add(answerContainer);
			}

			actionsContainer.getChildren().setAll(new HBox());

			// TODO: - Not a fan of the way this is done, but it'll do for now
			for (String permission : vm.getPermissionList()) {
				switch (permission) {
				case "DELETE":
					Button deleteQuestionButton = new Button("Delete");
					deleteQuestionButton.setOnAction(_ -> {
						System.err.println("pressed");
						vm.deleteQuestion();
					});
					
					actionsContainer.getChildren().add(deleteQuestionButton);
					break;
				case "COMMENT":					
					Button commentButton = new Button("Comment");

				    commentButton.setOnAction(_ -> {
				        if (!vm.getIsCommenting()) {
				            vm.setIsCommenting(true);
				            newCommentTextField.setVisible(true);
				            newCommentTextField.setManaged(true);
				            commentButton.setText("Post");
				        } else {
				            String text = newCommentTextField.getText().trim();
				            if (!text.isEmpty()) {
				                vm.addAnswer(text);
				                newCommentTextField.clear();
				                newCommentTextField.setVisible(false);
				                newCommentTextField.setManaged(false);
				                commentButton.setText("Comment");
				                vm.setIsCommenting(false);
				            } else {
				                System.out.println("Comment is empty!");
				            }
				        }
				    });

				    actionsContainer.getChildren().add(commentButton);
				    break;
				case "EDIT":
					updateButton.setOnMouseClicked(_ -> {
						boolean nowEditing = !vm.getIsEditingQuestion();
						vm.setIsEditingQuestion(!vm.getIsEditingQuestion());
						
						if (nowEditing) {
					        // Entering edit mode
							updateButton.setText("Commit Edit");

					        titleTextField.setText(vm.getQuestion().getTitle());
					        bodyTextField.setText(vm.getQuestion().getText());

					        titleLabel.setVisible(false);
					        titleTextField.setVisible(true);

					        bodyTextLabel.setVisible(false);
					        bodyTextField.setVisible(true);
					    } else {
					        // Committing edit
					    	updateButton.setText("Edit");

					        String newTitle = titleTextField.getText();
					        String newBody = bodyTextField.getText();
					        vm.updateQuestion(newTitle, newBody);

					        titleLabel.setText(newTitle);
					        bodyTextLabel.setText(newBody);

					        titleLabel.setVisible(true);
					        titleTextField.setVisible(false);

					        bodyTextLabel.setVisible(true);
					        bodyTextField.setVisible(false);
					    }
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
