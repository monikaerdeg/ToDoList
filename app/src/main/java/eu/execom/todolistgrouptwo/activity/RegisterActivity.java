package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.net.Network;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.model.User;
import eu.execom.todolistgrouptwo.model.dto.RegisterDTO;
import eu.execom.todolistgrouptwo.model.dto.TokenContainerDTO;
import eu.execom.todolistgrouptwo.util.NetworkingUtils;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {


    @ViewById
    EditText name;

    @ViewById
    EditText username;

    @ViewById
    EditText password;

    @RestService
    RestApi restApi;

    @EditorAction(R.id.password)
    @Click
    void register() {
        final String name = this.name.getText().toString();
        final String username = this.username.getText().toString();
        final String password = this.password.getText().toString();
        final User user = new User(name, username, password);

        registerUser(user);
    }

    @Background
    void registerUser(User user) {
        boolean successfulLogin;

        TokenContainerDTO tokenContainerDTO = null;

        try {
            final RegisterDTO registerDTO = new RegisterDTO(user.getUsername(), user.getPassword(), user.getPassword());

            final ResponseEntity userCreatedResponse = restApi.register(registerDTO);


            if(!userCreatedResponse.getStatusCode().equals(HttpStatus.OK)) {
                showRegisterError();
                return;
            }

            tokenContainerDTO = restApi.login
                    (NetworkingUtils.packUserCredentials(user.getUsername(),user.getPassword()));

            successfulLogin = tokenContainerDTO.getAccessToken() != null;

        }catch (RestClientException e){
            successfulLogin = false;
        }

        if (successfulLogin) {
            login(tokenContainerDTO);
        } else {
            showRegisterError();
        }
    }

    @UiThread
    void login(TokenContainerDTO tokenContainerDTO) {
        final Intent intent = new Intent();
        intent.putExtra("access_token", tokenContainerDTO.getAccessToken());

        setResult(RESULT_OK, intent);
        finish();
    }

    @UiThread
    void showRegisterError() {
        username.setError("Username already exists.");
    }


}
