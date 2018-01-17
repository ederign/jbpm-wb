/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.cm.client.roles;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.CommaListValuesConverter;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.uberfire.client.views.pfly.widgets.*;
import org.uberfire.mvp.Command;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class EditRoleAssignmentViewImpl extends AbstractView<CaseRolesPresenter> implements CaseRolesPresenter.EditRoleAssignmentView {

    @Inject
    @DataField("role-name-group")
    FormGroup roleNameGroup;

    @Inject
    @DataField("role-name-help")
    Span roleNameHelp;

    @Inject
    @Bound
    @DataField("role-name-text")
    Span name;

    @Inject
    @DataField("role-name-label")
    FormLabel roleNameLabel;

    @Inject
    @DataField("assignment-label")
    FormLabel assignmentLabel;

    @Inject
    @Bound(converter = CommaListValuesConverter.class)
    @DataField("user-name-input")
    TextInput users;

    @Inject
    @DataField("user-name-group")
    FormGroup userNameGroup;

    @Inject
    @Bound(converter = CommaListValuesConverter.class)
    @DataField("group-name-input")
    TextInput groups;

    @Inject
    @DataField("group-name-help")
    Span groupNameHelp;

    @Inject
    @DataField("group-name-group")
    FormGroup groupNameGroup;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Inject
    @DataField("alert")
    private InlineNotification notification;

    @Inject
    @DataField("roles-help")
    private Anchor rolesHelp;

    @Inject
    @DataField("modal")
    private Modal modal;

    @Inject
    @AutoBound
    private DataBinder<CaseRoleAssignmentSummary> binder;

    private Command okCommand;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        this.roleNameLabel.addRequiredIndicator();
        this.assignmentLabel.addRequiredIndicator();
        rolesHelp.setAttribute("data-content",
                               translationService.getTranslation(ROLES_INFO_TEXT));
        jQueryPopover.wrap(rolesHelp).popover();
        notification.setType(InlineNotification.InlineNotificationType.DANGER);
    }

    @Override
    public void init(final CaseRolesPresenter presenter) {
    }

    @Override
    public CaseRoleAssignmentSummary getValue() {
        return binder.getModel();
    }

    @Override
    public void setValue(final CaseRoleAssignmentSummary caseRoleAssignmentSummary) {
        binder.setModel(caseRoleAssignmentSummary);
    }

    @Override
    public void showValidationError(final List<String> messages) {
        notification.setMessage(messages);
        removeCSSClass(notification.getElement(),
                       "hidden");
    }

    @Override
    public void show(final Command okCommand) {
        clearErrorMessages();
        this.okCommand = okCommand;
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void setErrorState() {
        users.focus();
        userNameGroup.setValidationState(ValidationState.ERROR);
        groupNameGroup.setValidationState(ValidationState.ERROR);
    }

    private void clearErrorMessages() {
        addCSSClass(notification.getElement(),
                    "hidden");
        notification.setMessage("");
        roleNameHelp.setTextContent("");
        groupNameHelp.setTextContent("");
        roleNameGroup.clearValidationState();
        userNameGroup.clearValidationState();
        groupNameGroup.clearValidationState();
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    @EventHandler("assign")
    public void onAssignClick(final @ForEvent("click") MouseEvent event) {
        clearErrorMessages();
        Optional.ofNullable(okCommand).ifPresent(Command::execute);
    }

    @EventHandler("cancel")
    public void onCancelClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    @EventHandler("close")
    public void onCloseClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }
}