package UI.Elements;

import Controllers.PostDisplayViewModel;
import Models.Answer;
import Models.Question;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class PostCell extends ListCell<PostDisplayViewModel> {
	private final HBox outerMostContainer = new HBox();
	private final Region spacer = new Region();
	private final VBox layout = new VBox();

	private final Label titleLabel = new Label();
	private TextField titleTextField = new TextField();

	private final Label bodyTextLabel = new Label();
	private TextField bodyTextField = new TextField();

	private TextField newCommentTextField = new TextField();

	private final Label statusLabel = new Label();
	private final Label permissionLabel = new Label(); // This is for illustrative purposes
	private final HBox actionsContainer = new HBox();
	
	private StackPane titleStackPane = new StackPane(titleLabel, titleTextField);

	private final Button updateButton = new Button("Edit");

	public PostCell() {
		titleLabel.setWrapText(true);
		titleTextField.setVisible(false);
		bodyTextField.setVisible(false);
		
		titleStackPane.setAlignment(Pos.CENTER_LEFT);

		StackPane bodyStackPane = new StackPane(bodyTextLabel, bodyTextField);
		bodyStackPane.setAlignment(Pos.CENTER_LEFT);

		bodyTextLabel.setWrapText(true);
		permissionLabel.setWrapText(true);

		newCommentTextField.setVisible(false);
		newCommentTextField.setManaged(false);

		layout.getChildren().addAll(titleStackPane, bodyStackPane, newCommentTextField, statusLabel, actionsContainer);
		layout.setFillWidth(true);
		layout.setMaxWidth(Double.MAX_VALUE);

		// Attach stylesheet
		layout.getStylesheets().add(getClass().getResource("/Styles/styles.css").toExternalForm());

		// Assign style classes
		layout.getStyleClass().add("question-cell");
		titleLabel.getStyleClass().add("question-title");
		bodyTextLabel.getStyleClass().add("question-body");
		statusLabel.getStyleClass().add("question-status");
		
		outerMostContainer.getChildren().addAll(spacer, layout);
		setGraphic(outerMostContainer);
	}

	@Override
	protected void updateItem(PostDisplayViewModel vm, boolean empty) {
	    super.updateItem(vm, empty);

	    if (empty || vm.getPost() == null) {
	        setGraphic(null);
	        return;
	    }

	    actionsContainer.getChildren().clear();

	    newCommentTextField.setVisible(false);
	    newCommentTextField.setManaged(false);

	    newCommentTextField.clear();

	    if (vm.isQuestion()) {
	        titleLabel.setText(vm.getPost().getTitleText());
	        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
	        statusLabel.setText(formatStatus((Question) vm.getPost()));

	        titleLabel.setVisible(true);
	        titleLabel.setManaged(true);

	        statusLabel.setVisible(true);
	        statusLabel.setManaged(true);
	        
	        spacer.setPrefWidth(0);
	    } else {
	    	int leftPadding = (((Answer) vm.getPost()).getReplyDepth() + 1) * 40;
	    	spacer.setPrefWidth(leftPadding);
	        
	    	layout.getChildren().remove(titleStackPane);
	        titleLabel.setVisible(false);
	        titleLabel.setManaged(false);

	        statusLabel.setVisible(false);
	        statusLabel.setManaged(false);
	    }
	    
	    if((vm.getIsDeleted())) {
	    	System.out.println("Haha");
    		bodyTextLabel.setText("This message was deleted");
    	} else {
    		bodyTextLabel.setText(Utils.StringUtils.truncate(vm.getPost().getBodyText(), 150));
    	}

	    for (String permission : vm.getPermissionList()) {
	        switch (permission) {
	            case "DELETE":
	                Button deleteBtn = new Button("Delete");
	                deleteBtn.setOnAction(_ -> vm.deletePost());
	                actionsContainer.getChildren().add(deleteBtn);
	                break;
	            case "COMMENT":
	                Button commentBtn = new Button("Comment");
	                commentBtn.setOnAction(_ -> {
	                    if (!vm.getIsCommenting().get()) {
	                        vm.setIsCommenting(true);
	                        newCommentTextField.setVisible(true);
	                        newCommentTextField.setManaged(true);
	                        commentBtn.setText("Post");
	                    } else {
	                        String newCommentText = newCommentTextField.getText().trim();
	                        if (!newCommentText.isEmpty()) {
	                            vm.addAnswer(null, newCommentText);
	                            newCommentTextField.clear();
	                            newCommentTextField.setVisible(false);
	                            newCommentTextField.setManaged(false);
	                            commentBtn.setText("Comment");
	                            vm.setIsCommenting(false);
	                        } else {
	                            System.out.println("Comment is empty!");
	                        }
	                    }
	                });
	                actionsContainer.getChildren().add(commentBtn);
	                break;
	            case "EDIT":
	                updateButton.setText("Edit");
	                updateButton.setOnMouseClicked(_ -> {
	                    boolean nowEditing = !vm.getIsEditingQuestion();
	                    vm.setIsEditing(nowEditing);

	                    if (nowEditing) {
	                        updateButton.setText("Commit Edit");
	                        titleTextField.setText(vm.getPost().getTitleText());
	                        bodyTextField.setText(vm.getPost().getBodyText());
	                        titleLabel.setVisible(false);
	                        titleTextField.setVisible(true);
	                        bodyTextLabel.setVisible(false);
	                        bodyTextField.setVisible(true);
	                    } else {
	                        updateButton.setText("Edit");
	                        String newTitle = titleTextField.getText();
	                        String newBody = bodyTextField.getText();
	                        vm.updatePost(newTitle, newBody);
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
	    
	    setGraphic(outerMostContainer);
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
