package org.peerbox.presenter.validation;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.peerbox.model.UserManager;
import org.peerbox.presenter.validation.ValidationUtils.ValidationResult;

public final class UsernameValidator extends TextFieldValidator {

	private UserManager userManager;

	public UsernameValidator(TextField txtUsername, StringProperty errorProperty, UserManager userManager) {
		super(txtUsername, errorProperty, true);
		this.userManager = userManager;
	}
	
	@Override
	public ValidationResult validate(final String username) {
		return validate(username, false);
	}
	
	public ValidationResult validate(boolean checkIfRegistered) {
		return validate(validateTxtField.getText(), checkIfRegistered);
	}

	public ValidationResult validate(String username, boolean checkIfRegistered) {
		try {
			
			if(username == null) {
				return ValidationResult.ERROR;
			}

			final String usernameTr = username.trim();
			ValidationResult res = ValidationUtils.validateUsername(usernameTr,
					checkIfRegistered, userManager);

			if (res.isError()) {
				setErrorMessage(res.getMessage());
				decorateError();
			} else {
				clearErrorMessage();
				undecorateError();
			}
			
			return res;

		} catch (NoPeerConnectionException e) {
			setErrorMessage("Network connection failed.");
		}

		return ValidationResult.ERROR;
	}

	public void reset() {
		undecorateError();
		clearErrorMessage();
	}
}