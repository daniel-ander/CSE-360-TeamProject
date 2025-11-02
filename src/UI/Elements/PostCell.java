package UI.Elements;

import Controllers.PostDisplayViewModel;

import javafx.beans.binding.Bindings;
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

    private final Text bodyText = new Text();
    private final TextArea bodyTextField = new TextArea();

    private final TextArea newCommentTextField = new TextArea();

    private final Label statusLabel = new Label();
    private final Label permissionLabel = new Label(); // optional illustrative
    private final HBox actionsContainer = new HBox();

    private final StackPane titleStackPane = new StackPane(titleLabel, titleTextField);

    private final Button updateButton = new Button("Edit");
    private final Button toggleExpandButton = new Button("Expand");

    public PostCell() {
        // Setup wrapping and alignment
    	bodyTextField.setWrapText(true);
    	bodyTextField.setPrefRowCount(1); // start as one line

    	bodyTextField.textProperty().addListener((obs, oldText, newText) -> {
    	    Text text = new Text(newText);
    	    text.setFont(bodyTextField.getFont());
    	    double height = text.getLayoutBounds().getHeight() + 20; // add padding
    	    bodyTextField.setPrefHeight(height);
    	});
    	
        bodyText.setFont(bodyTextField.getFont());
        bodyText.wrappingWidthProperty().bind(bodyTextField.widthProperty().subtract(20));
        bodyTextField.setWrapText(true);

        titleLabel.setWrapText(true);
        titleTextField.setVisible(false);
        bodyTextField.setVisible(false);

        titleStackPane.setAlignment(Pos.CENTER_LEFT);
        StackPane bodyStackPane = new StackPane(bodyText, bodyTextField);
        bodyStackPane.setAlignment(Pos.CENTER_LEFT);

        permissionLabel.setWrapText(true);

        newCommentTextField.setVisible(false);
        newCommentTextField.setManaged(false);

        layout.getChildren().addAll(titleStackPane, bodyStackPane, newCommentTextField, statusLabel, actionsContainer);
        layout.setFillWidth(true);
        layout.setMaxWidth(Double.MAX_VALUE);

        // Apply stylesheet
        layout.getStylesheets().add(getClass().getResource("/Styles/styles.css").toExternalForm());

        // Style classes
        layout.getStyleClass().add("question-cell");
        titleLabel.getStyleClass().add("question-title");
        bodyText.getStyleClass().add("question-body");
        statusLabel.getStyleClass().add("question-status");

        outerMostContainer.getChildren().addAll(spacer, layout);
        setGraphic(outerMostContainer);
    }

    @Override
    protected void updateItem(PostDisplayViewModel vm, boolean empty) {
        super.updateItem(vm, empty);

        // Clean up old bindings and state
        if (currentVm != null) {
            try { titleLabel.textProperty().unbind(); } catch (Exception ignored) {}
            try { bodyText.textProperty().unbind(); } catch (Exception ignored) {}
        }

        titleTextField.clear();
        bodyTextField.clear();
        titleLabel.setText("");
        bodyText.setText("");

        if (empty || vm == null || vm.getPost() == null) {
            setGraphic(null);
            currentVm = null;
            return;
        }

        // Assign current ViewModel
        currentVm = vm;

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

        // Manage visibility for edit vs view mode
        titleLabel.visibleProperty().bind(vm.getIsEditingPostProperty().not());
        titleTextField.visibleProperty().bind(vm.getIsEditingPostProperty());
        bodyText.visibleProperty().bind(vm.getIsEditingPostProperty().not());
        bodyTextField.visibleProperty().bind(vm.getIsEditingPostProperty());

        // Handle edit commit/update
        vm.getIsEditingPostProperty().addListener((obs, wasEditing, isEditing) -> {
            if (isEditing) {
                // Load fresh values from VM when entering edit mode
                titleTextField.setText(vm.getLabelTextProperty().get());
                bodyTextField.setText(vm.getBodyTextProperty().get());
            } else {
                // Commit changes back to VM when exiting edit mode
                vm.setLabelTextProperty(titleTextField.getText());
                vm.setBodyTextProperty(bodyTextField.getText());
                vm.updatePost(titleTextField.getText(), bodyTextField.getText());
                titleLabel.setText(titleTextField.getText());
                bodyText.setText(bodyTextField.getText());
            }
        });

        // Comment field visibility
        newCommentTextField.visibleProperty().bind(vm.getIsCommentingProperty());
        newCommentTextField.managedProperty().bind(vm.getIsCommentingProperty());
        newCommentTextField.clear();

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

        // Deleted posts handling
        if (vm.getIsDeleted()) {
            bodyText.setText("This message was deleted");
            bodyTextField.setText("This message was deleted");
        } else {
            String truncated = Utils.StringUtils.truncate(vm.getPost().getBodyText(), 150);
            bodyText.setText(truncated);
            bodyTextField.setText(truncated);
        }

        // Build actions
        actionsContainer.getChildren().clear();

        for (String permission : vm.getPermissionList()) {
            switch (permission) {
                case "DELETE":
                    Button deleteBtn = new Button("Delete");
                    deleteBtn.setOnAction(_ -> vm.deletePost());
                    actionsContainer.getChildren().add(deleteBtn);
                    break;

                case "COMMENT":
                    Button commentBtn = new Button("Comment");
                    commentBtn.textProperty().bind(
                            Bindings.when(vm.getIsCommentingProperty())
                                    .then("Post")
                                    .otherwise("Comment")
                    );
                    commentBtn.setOnAction(_ -> {
                        if (!vm.getIsCommentingProperty().get()) {
                            vm.setIsCommentingProperty(true);
                        } else {
                            String newCommentText = newCommentTextField.getText().trim();
                            if (!newCommentText.isEmpty()) {
                                vm.addAnswer(null, newCommentText);
                                newCommentTextField.clear();
                                vm.setIsCommentingProperty(false);
                            } else {
                                System.out.println("Comment is empty!");
                            }
                        }
                    });
                    actionsContainer.getChildren().add(commentBtn);
                    break;

                case "EDIT":
                    updateButton.textProperty().bind(
                            Bindings.when(vm.getIsEditingPostProperty())
                                    .then("Commit Edit")
                                    .otherwise("Edit")
                    );

                    updateButton.setOnAction(_ -> vm.setIsEditing(!vm.getIsEditingPostProperty().get()));
                    actionsContainer.getChildren().add(updateButton);
                    break;
            }
        }

        // Expand / Collapse button
        toggleExpandButton.textProperty().bind(
        		Bindings.when(vm.getExpandedProperty()).then("Collapse").otherwise("Expand")
        );
        toggleExpandButton.visibleProperty().bind(vm.getDoesHaveChildren());
        toggleExpandButton.managedProperty().bind(vm.getDoesHaveChildren());
        toggleExpandButton.setOnAction(_ -> vm.toggleExpand());
        actionsContainer.getChildren().add(toggleExpandButton);

        setGraphic(outerMostContainer);
    }

    public Button getToggleExpandButton() {
        return toggleExpandButton;
    }
}
