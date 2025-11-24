package UI.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import Controllers.ForumPage.PostDisplayViewModel;
import Models.IPost;
import Models.IPost.Type;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class PostCell extends ListCell<PostDisplayViewModel> {

    private final HBox outerMostContainer = new HBox();
    private PostDisplayViewModel currentVm;

    private final Region spacer = new Region();
    private final VBox layout = new VBox();

    private final HBox starsLayout = new HBox();
    private Integer hoveredStarIndex = null;
    private final List<StackPane> interactiveStars = new ArrayList<>();

    private final Label titleLabel = new Label();
    private final TextField titleTextField = new TextField();

    private final StackPane bodyStackPane = new StackPane();
    private final Text bodyText = new Text();
    private final TextArea bodyTextField = new TextArea();

    private final TextArea newCommentTextField = new TextArea();
    private final TextArea newReviewTextField = new TextArea();

    private final Button favorReviewerButton = new Button();
    private final Label statusLabel = new Label();
    private final Label ratingsLabel = new Label();

    private final Label permissionLabel = new Label();
    private final HBox actionsContainer = new HBox();

    private final StackPane titleStackPane = new StackPane(titleLabel, titleTextField);
    
    private Integer currentlyHoveredOnStarIndex = null;

    public PostCell() {
        setInitialProperties();
        setInitialListeners();

        HBox ratingProperties = new HBox();
        ratingProperties.getChildren().addAll(starsLayout, ratingsLabel);

        bodyStackPane.getChildren().addAll(bodyText, bodyTextField);

        layout.getChildren().addAll(titleStackPane, bodyStackPane, newCommentTextField,
                newReviewTextField, statusLabel, ratingProperties, actionsContainer);

        outerMostContainer.getChildren().addAll(spacer, layout);
        setGraphic(outerMostContainer);
    }

    @Override
    protected void updateItem(PostDisplayViewModel vm, boolean empty) {
        super.updateItem(vm, empty);

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

        currentVm = vm;

        ratingsLabel.textProperty().bind(
        	    Bindings.createStringBinding(() -> {
        	        BigDecimal rating = vm.getRatingProperty().get();
        	        if (rating == null) return "-";
        	        double stars = rating.doubleValue() * vm.getNumberOfRatingStars();
        	        return String.format("%.2f", stars);
        	    }, vm.getRatingProperty())
        	);


        buildInteractiveStarRow(vm);

        favorReviewerButton.textProperty().bind(
                Bindings.when(vm.getIsFavoredReviewerProperty())
                        .then("Unfavor This Reviewer")
                        .otherwise("Favor This Reviewer"));

        favorReviewerButton.setOnMouseClicked(_ -> {
            vm.toggleFavoredReviewerStatus(!vm.getIsFavoredReviewerProperty().get());
        });

        setStyles(vm);
        populateTextFields(vm);
        populateActionsContainer(vm);
        setUpBindings(vm);

        vm.getIsEditingPostProperty().addListener(editingListener);

        setGraphic(outerMostContainer);
    }

    private void setStyles(PostDisplayViewModel vm) {
        layout.getStylesheets().add(
                getClass().getResource("/Styles/styles.css").toExternalForm());

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
        titleTextField.clear();
        bodyTextField.clear();
        titleLabel.setText("");
        bodyText.setText("");

        String title = vm.getLabelTextProperty().get();
        String body = vm.getBodyTextProperty().get();

        if (vm.isQuestion() && (title == null || title.isEmpty())) {
            title = vm.getMainLabelText();
        }
        if (body == null || body.isEmpty()) {
            body = vm.getPost().getBodyText();
        }

        titleTextField.setText(title);
        bodyTextField.setText(body);
        titleLabel.setText(title);
        bodyText.setText(body);

        newCommentTextField.clear();
        newReviewTextField.clear();

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

        if (vm.getIsDeleted()) {
            bodyText.setText("This message was deleted");
            bodyTextField.setText("This message was deleted");
        }
    }

    private void populateActionsContainer(PostDisplayViewModel vm) {
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
                    commentButton.textProperty().bind(
                            Bindings.when(vm.getIsCommentingProperty()).then("Post").otherwise("Comment"));

                    commentButton.setOnAction(_ -> {
                        if (!vm.getIsCommentingProperty().get()) {
                            vm.setIsCommentingProperty(true);
                        } else {
                            String text = newCommentTextField.getText().trim();
                            if (!text.isEmpty()) {
                                vm.addAnswer(null, text, IPost.Type.Answer);
                                newCommentTextField.clear();
                                vm.setIsCommentingProperty(false);
                            }
                        }
                    });

                    actionsContainer.getChildren().add(commentButton);
                    break;

                case "REVIEW":
                    Button reviewButton = new Button("Review");
                    reviewButton.textProperty().bind(
                            Bindings.when(vm.getIsReviewingProperty()).then("Post Review").otherwise("Review"));

                    reviewButton.setOnAction(_ -> {
                        if (!vm.getIsReviewingProperty().get()) {
                            vm.setIsReviewingProperty(true);
                        } else {
                            String text = newReviewTextField.getText().trim();
                            if (!text.isEmpty()) {
                                vm.addAnswer(null, text, IPost.Type.Review);
                                newReviewTextField.clear();
                                vm.setIsReviewingProperty(false);
                            }
                        }
                    });
                    actionsContainer.getChildren().add(reviewButton);
                    break;

                case "EDIT":
                    Button updateButton = new Button("Edit");
                    updateButton.textProperty().bind(
                            Bindings.when(vm.getIsEditingPostProperty()).then("Commit Edit").otherwise("Edit"));

                    updateButton.setOnAction(_ -> {
                        if (vm.getIsEditingPostProperty().get()) {
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

        Button toggleExpandButton = new Button();
        toggleExpandButton.textProperty().bind(
                Bindings.when(vm.getExpandedProperty()).then("Collapse").otherwise("Expand"));

        toggleExpandButton.visibleProperty().bind(vm.getDoesHaveChildren());
        toggleExpandButton.managedProperty().bind(vm.getDoesHaveChildren());

        toggleExpandButton.setOnAction(_ -> vm.toggleExpand());
        actionsContainer.getChildren().add(toggleExpandButton);
    }


    private final ChangeListener<Boolean> editingListener = (_, _, isEditing) -> {
        if (currentVm == null) return;

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


    private final ChangeListener<String> bodyTextHeightChangeListener =
            (_, _, newText) -> {
                Text text = new Text(newText);
                text.setFont(bodyTextField.getFont());
                double height = text.getLayoutBounds().getHeight() + 20;
                bodyTextField.setPrefHeight(height);
            };

    private void setInitialProperties() {
        bodyTextField.setWrapText(true);
        bodyTextField.setPrefRowCount(1);
        bodyTextField.setVisible(false);

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
        newCommentTextField.visibleProperty().bind(vm.getIsCommentingProperty());
        newCommentTextField.managedProperty().bind(vm.getIsCommentingProperty());

        newReviewTextField.visibleProperty().bind(vm.getIsReviewingProperty());
        newReviewTextField.managedProperty().bind(vm.getIsReviewingProperty());

        titleLabel.visibleProperty().bind(vm.getIsEditingPostProperty().not());
        titleTextField.visibleProperty().bind(vm.getIsEditingPostProperty());

        bodyText.visibleProperty().bind(vm.getIsEditingPostProperty().not());
        bodyTextField.visibleProperty().bind(vm.getIsEditingPostProperty());

        favorReviewerButton.visibleProperty().bind(vm.getShouldDisplayFavoredReviewerButtonProperty());
        favorReviewerButton.managedProperty().bind(vm.getShouldDisplayFavoredReviewerButtonProperty());

        actionsContainer.getChildren().add(favorReviewerButton);
    }

    private void setInitialListeners() {
        bodyTextField.textProperty().addListener(bodyTextHeightChangeListener);
    }

    private HBox buildInteractiveStarRow(PostDisplayViewModel vm) {
        starsLayout.getChildren().clear();
        interactiveStars.clear();
        starsLayout.setAlignment(Pos.CENTER_LEFT);

        ObjectProperty<BigDecimal> ratingProperty = vm.getRatingProperty();

        for (int i = 1; i <= vm.getNumberOfRatingStars(); i++) {
            final int starIndex = i;

            StackPane star = createInteractiveStar(
                    () -> vm.setRatingProperty(BigDecimal.valueOf(starIndex)),
                    starIndex,
                    vm);

            star.setOnMouseEntered(e -> {
                hoveredStarIndex = starIndex;
                refreshStarFills(vm);
            });

            star.setOnMouseExited(e -> {
                hoveredStarIndex = null;
                refreshStarFills(vm);
            });

            interactiveStars.add(star);
            starsLayout.getChildren().add(star);
        }

        ratingProperty.addListener((obs, oldV, newV) -> refreshStarFills(vm));
        refreshStarFills(vm);

        return starsLayout;
    }

    private StackPane createInteractiveStar(
            Runnable onClick,
            int starIndex,
            PostDisplayViewModel vm
    ) {
        Polygon outline = buildStarPolygon();
        Polygon fill = buildStarPolygon();

        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(Color.GOLD);
        outline.setStrokeWidth(2);

        fill.setFill(Color.GOLD);

        Rectangle clip = new Rectangle();
        fill.setClip(clip);

        fill.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setX(0);
            clip.setY(0);
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
        });

        StackPane pane = new StackPane(outline, fill);
        pane.setBackground(Background.EMPTY);

        pane.setScaleX(0.5);
        pane.setScaleY(0.5);

        pane.setOnMouseClicked(e -> onClick.run());

        return pane;
    }

    private void refreshStarFills(PostDisplayViewModel vm) {
        if (interactiveStars.isEmpty()) return;

        BigDecimal rating = vm.getStarRating();
        if (rating == null) rating = BigDecimal.ZERO;

        int totalStars = vm.getNumberOfRatingStars();
        double starsFilled = rating.doubleValue();
        
        System.out.println(vm.getPost().getId() + "______________");

        for (int i = 1; i <= totalStars; i++) {
            StackPane starStackPane = interactiveStars.get(i - 1);

            double fillAmount;
            if (hoveredStarIndex != null) {
                fillAmount = (i <= hoveredStarIndex) ? 1.0 : 0.0;
                ((Polygon)starStackPane.getChildren().get(1)).setFill(Color.BURLYWOOD);
            } else {
                fillAmount = Math.min(Math.max(starsFilled - (i - 1), 0), 1);
                ((Polygon)starStackPane.getChildren().get(1)).setFill(Color.GOLD);
            }
            applyFillToStar(starStackPane, fillAmount);
        }
        
        System.out.println(vm.getPost().getId() + "______________");
    }

    private void applyFillToStar(StackPane starStackPane, double fillFraction) {
        Polygon starFill = (Polygon) starStackPane.getChildren().get(1);
        Rectangle clip = (Rectangle) starFill.getClip();

        Bounds lb = starFill.getLayoutBounds();
        
        clip.setX(lb.getMinX());
        clip.setY(lb.getMinY());
        clip.setWidth(lb.getWidth() * fillFraction);
        clip.setHeight(lb.getHeight());
    }


    private Polygon buildStarPolygon() {
        Polygon star = new Polygon();
        star.getPoints().addAll(
                0.0, -20.0,
                6.0, -6.0,
                20.0, -6.0,
                9.0, 3.0,
                12.0, 18.0,
                0.0, 9.0,
                -12.0, 18.0,
                -9.0, 3.0,
                -20.0, -6.0,
                -6.0, -6.0
        );
        return star;
    }
}
