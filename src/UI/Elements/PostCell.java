package UI.Elements;

import org.mockito.Mockito;

import Controllers.ForumPage.PostDisplayViewModel;
import Models.IPost;
import Models.IPost.Type;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class PostCell extends ListCell<PostDisplayViewModel> {
	private final HBox outerMostContainer = new HBox();
	private PostDisplayViewModel currentVm;
	private final Region spacer = new Region();
	private final VBox layout = new VBox();

	private final Label titleLabel = new Label();
	private final TextField titleTextField = new TextField();

	private final StackPane bodyStackPane = new StackPane();

	private final Text bodyText = new Text();
	private final TextArea bodyTextField = new TextArea();

	private final TextArea newCommentTextField = new TextArea();

	private final TextArea newReviewTextField = new TextArea();

	private final Label statusLabel = new Label();
	private final Label permissionLabel = new Label();
	private final HBox actionsContainer = new HBox();

	private final StackPane titleStackPane = new StackPane(titleLabel, titleTextField);

	public PostCell() {
		setInitialProperties();
		setInitialListeners();

		bodyStackPane.getChildren().addAll(bodyText, bodyTextField);
		layout.getChildren().addAll(titleStackPane, bodyStackPane, newCommentTextField, newReviewTextField, statusLabel,
				actionsContainer);
		outerMostContainer.getChildren().addAll(spacer, layout);

		setGraphic(outerMostContainer);
	}

	@Override
	protected void updateItem(PostDisplayViewModel vm, boolean empty) {
		super.updateItem(vm, empty);

		// Clean up old bindings, listeners and state
		if (currentVm != null) {
			titleLabel.textProperty().unbind();
			bodyText.textProperty().unbind();

			currentVm.getIsEditingPostProperty().removeListener(editingListener);
		}

		if (empty || vm == null || vm.getPost() == null) {
			setGraphic(null);
			currentVm = null;
			return;
		}

		// Assign current ViewModel
		currentVm = vm;
		
		setStyles(currentVm);

		populateTextFields(currentVm);
		populateActionsContainer(currentVm);
		setUpBindings(currentVm);

		currentVm.getIsEditingPostProperty().addListener(editingListener);

		setGraphic(outerMostContainer);
	}

	private void setStyles(PostDisplayViewModel vm) {
		// Apply style sheet
		layout.getStylesheets().add(getClass().getResource("/Styles/styles.css").toExternalForm());

		// Style classes
		layout.getStyleClass().add("question-cell");
		titleLabel.getStyleClass().add("question-title");
		bodyText.getStyleClass().add("question-body");
		statusLabel.getStyleClass().add("question-status");
		
		if (vm.getPost().getPostType() == Type.Review) {
			outerMostContainer.setStyle("-fx-background-color: #5189A1; -fx-text-fill: white;");
		} else {
			outerMostContainer.setStyle("");
		}
	}

	private void populateTextFields(PostDisplayViewModel vm) {
		// Reset fields
		titleTextField.clear();
		bodyTextField.clear();
		titleLabel.setText("");
		bodyText.setText("");

		// Load text values manually
		String title = vm.getLabelTextProperty().get();
		String body = vm.getBodyTextProperty().get();

		if (vm.isQuestion()) {
			if (title == null || title.isEmpty()) {
				title = vm.getMainLabelText();
			}
		}

		if (body == null || body.isEmpty()) {
			body = vm.getPost().getBodyText();
		}

		// Set text fields and display labels
		titleTextField.setText(title);
		bodyTextField.setText(body);
		titleLabel.setText(title);
		bodyText.setText(body);

		newCommentTextField.clear();
		newReviewTextField.clear();

		// Configure layout depending on post type
		if (vm.isQuestion()) {
			titleStackPane.setVisible(true);
			titleStackPane.setManaged(true);
			statusLabel.setVisible(true);
			statusLabel.setManaged(true);
			spacer.setPrefWidth(0);
		} else {
			titleStackPane.setVisible(false);
			titleStackPane.setManaged(false);
			statusLabel.setVisible(false);
			statusLabel.setManaged(false);
			spacer.setPrefWidth(vm.getDepthLevel() * 40);
		}

		// Get deleted status from database and display body text accordingly
		if (vm.getIsDeleted()) {
			bodyText.setText("This message was deleted");
			bodyTextField.setText("This message was deleted");
		} else {
			bodyText.setText(vm.getPost().getBodyText());
			bodyTextField.setText(vm.getPost().getBodyText());
		}
	}

	private void populateActionsContainer(PostDisplayViewModel vm) {
		// Build actions
		actionsContainer.getChildren().clear();

		for (String permission : vm.getPermissionList()) {
			switch (permission) {
			case "DELETE":
				Button deleteButton = new Button("Delete");
				deleteButton.setOnAction(_ -> {
					vm.deletePost();

					if (vm.getIsDeletedProperty().get()) {
						bodyText.setText("This message was deleted");
						bodyTextField.setText("This message was deleted");
					}
				});

				actionsContainer.getChildren().add(deleteButton);
				break;

			case "COMMENT":
				Button commentButton = new Button("Comment");
				commentButton.textProperty()
						.bind(Bindings.when(vm.getIsCommentingProperty()).then("Post").otherwise("Comment"));

				commentButton.setOnAction(_ -> {
					if (!vm.getIsCommentingProperty().get()) {
						vm.setIsCommentingProperty(true);
					} else {
						String newCommentText = newCommentTextField.getText().trim();
						if (!newCommentText.isEmpty()) {
							vm.addAnswer(null, newCommentText, IPost.Type.Answer);
							newCommentTextField.clear();
							vm.setIsCommentingProperty(false);
						} else {
							System.out.println("Comment is empty!");
						}
					}
				});
				actionsContainer.getChildren().add(commentButton);
				break;

			case "REVIEW":
				Button reviewButton = new Button("Review");
				reviewButton.textProperty()
						.bind(Bindings.when(vm.getIsReviewingProperty()).then("Post Review").otherwise("Review"));
				reviewButton.setOnAction(_ -> {
					if (!vm.getIsReviewingProperty().get()) {
						// Commit review
						vm.setIsReviewingProperty(true);
					} else {
						String newReviewText = newReviewTextField.getText().trim();
						if (!newReviewText.isEmpty()) {
							vm.addAnswer(null, newReviewText, IPost.Type.Review);
							newReviewTextField.clear();
							vm.setIsReviewingProperty(false);
						} else {
							System.out.println("Review is empty!");
						}
					}

				});
				actionsContainer.getChildren().add(reviewButton);
				break;

			case "EDIT":
				Button updateButton = new Button("Edit");
				updateButton.textProperty()
						.bind(Bindings.when(vm.getIsEditingPostProperty()).then("Commit Edit").otherwise("Edit"));

				updateButton.setOnAction(_ -> {
					if (vm.getIsEditingPostProperty().get()) {
						// Commit manually, then exit edit mode
						vm.updatePost(titleTextField.getText(), bodyTextField.getText());
						vm.setIsEditingProperty(false);
					} else {
						vm.setIsEditingProperty(true);
					}
				});
				actionsContainer.getChildren().add(updateButton);
				break;

			}
		}

		// Expand / Collapse button
		Button toggleExpandButton = new Button();

		toggleExpandButton.textProperty()
				.bind(Bindings.when(vm.getExpandedProperty()).then("Collapse").otherwise("Expand"));
		toggleExpandButton.visibleProperty().bind(vm.getDoesHaveChildren());
		toggleExpandButton.managedProperty().bind(vm.getDoesHaveChildren());
		toggleExpandButton.setOnAction(_ -> vm.toggleExpand());
		actionsContainer.getChildren().add(toggleExpandButton);
	}

	// Keeping track of the listener to add and remove it to adhere to cell reuse
	// logic
	private final ChangeListener<Boolean> editingListener = (_, _, isEditing) -> {
		if (currentVm == null)
			return;

		if (isEditing) {
			titleTextField.setText(currentVm.getLabelTextProperty().get());
			bodyTextField.setText(currentVm.getBodyTextProperty().get());
		} else {
			currentVm.setLabelTextProperty(titleTextField.getText());
			currentVm.setBodyTextProperty(bodyTextField.getText());
			titleLabel.setText(titleTextField.getText());
			bodyText.setText(bodyTextField.getText());
		}
	};

	// Needed to dynamically allocate the height of the body text
	private final ChangeListener<String> bodyTextHeightChangeListener = (_, _, newText) -> {
		Text text = new Text(newText);
		text.setFont(bodyTextField.getFont());
		double height = text.getLayoutBounds().getHeight() + 20; // add padding
		bodyTextField.setPrefHeight(height);
	};

	private void setInitialProperties() {
		// Setup wrapping and alignment
		bodyTextField.setWrapText(true);
		bodyTextField.setPrefRowCount(1); // start as one line
		bodyTextField.setVisible(false);
		bodyTextField.setWrapText(true);

		bodyText.setFont(bodyTextField.getFont());
		bodyText.wrappingWidthProperty().bind(bodyTextField.widthProperty().subtract(20));

		titleLabel.setWrapText(true);

		titleTextField.setVisible(false);

		titleStackPane.setAlignment(Pos.CENTER_LEFT);

		permissionLabel.setWrapText(true);

		newCommentTextField.setVisible(false);
		newCommentTextField.setManaged(false);

		newReviewTextField.setVisible(false);
		newReviewTextField.setManaged(false);

		layout.setFillWidth(true);
		layout.setMaxWidth(Double.MAX_VALUE);

		bodyStackPane.setAlignment(Pos.CENTER_LEFT);
	}

	private void setUpBindings(PostDisplayViewModel vm) {
		// Comment field visibility
		newCommentTextField.visibleProperty().bind(vm.getIsCommentingProperty());
		newCommentTextField.managedProperty().bind(vm.getIsCommentingProperty());

		// Review field visibility
		newReviewTextField.visibleProperty().bind(vm.getIsReviewingProperty());
		newReviewTextField.managedProperty().bind(vm.getIsReviewingProperty());

		// Manage visibility for edit vs view mode
		titleLabel.visibleProperty().bind(vm.getIsEditingPostProperty().not());
		titleTextField.visibleProperty().bind(vm.getIsEditingPostProperty());

		bodyText.visibleProperty().bind(vm.getIsEditingPostProperty().not());
		bodyTextField.visibleProperty().bind(vm.getIsEditingPostProperty());
	}

	private void setInitialListeners() {
		bodyTextField.textProperty().addListener(bodyTextHeightChangeListener);
	}

}
