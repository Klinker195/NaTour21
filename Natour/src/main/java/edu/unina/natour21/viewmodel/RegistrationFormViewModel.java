package edu.unina.natour21.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unina.natour21.dto.UserDTO;
import edu.unina.natour21.retrofit.AmazonAPI;
import edu.unina.natour21.retrofit.IUserAPI;
import edu.unina.natour21.view.activity.PostFilteringMapsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFormViewModel extends ViewModelBase {

    private static final String TAG = RegistrationFormViewModel.class.getSimpleName();

    private final MutableLiveData<Boolean> onSaveUser = new MutableLiveData<>();
    private static String userEmail;

    private static void setUserEmail(String email) {
        userEmail = email;
    }

    public void saveNewUser(String nickname, String name, String surname, Integer sex, String birthdate, Integer height, Float weight) {
        IUserAPI userAPI = AmazonAPI.getClient().create(IUserAPI.class);

        Amplify.Auth.fetchUserAttributes(
                new Consumer<List<AuthUserAttribute>>() {
                    @Override
                    public void accept(@NonNull List<AuthUserAttribute> value) {
                        for (int i = 0; i < value.size(); i++) {
                            if (value.get(i).getKey().getKeyString().equals("email")) {
                                Log.i("AuthAttributes", "Key: " + value.get(i).getKey().getKeyString() + " | Value: " + value.get(i).getValue());
                                RegistrationFormViewModel.setUserEmail(value.get(i).getValue());
                                RegistrationFormViewModel.userEmail = RegistrationFormViewModel.userEmail.replace(" ", "").toLowerCase();

                                UserDTO newUser = new UserDTO();
                                newUser.setEmail(RegistrationFormViewModel.userEmail);
                                newUser.setNickname(nickname);
                                newUser.setName(name);
                                newUser.setSurname(surname);
                                newUser.setSex(sex);
                                newUser.setBirthdate(birthdate);
                                newUser.setHeight(height);
                                newUser.setWeight(weight);

                                Call<UserDTO> call = userAPI.saveNewUser(newUser);
                                call.enqueue(new Callback<UserDTO>() {
                                    @Override
                                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                        UserDTO userDTO = new UserDTO();

                                        if (response.body() != null) {
                                            userDTO = response.body();

                                            Log.i("AmazonAPI", "User added successfully");
                                            Log.i("AmazonAPI", String.valueOf(userDTO.getName() != null) + " " + String.valueOf(userDTO.getEmail() != null));
                                            if (userDTO.getName() != null && userDTO.getEmail() != null)
                                                Log.i("AmazonAPI", userDTO.getEmail() + " " + userDTO.getName());
                                            onSaveUser(true);

                                            return;
                                        }

                                        Log.e("AmazonAPI", "Error adding user");
                                        onSaveUser(false);
                                    }

                                    @Override
                                    public void onFailure(Call<UserDTO> call, Throwable t) {
                                        Log.e("AmazonAPI", "Error adding user");
                                        t.printStackTrace();
                                        onSaveUser(false);
                                    }
                                });
                            }
                        }
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        Log.e("NatourAuth", value.toString());
                    }
                }
        );

    }

    public boolean checkNicknameFieldValidity(String nickname) {
        boolean check = false;

        if (!nickname.isEmpty()) {
            Pattern pattern = Pattern.compile("^[^0-9][^@#]+$");
            Matcher matcher = pattern.matcher(nickname);
            if (matcher.matches()) {
                check = true;
            }
        }

        return check;
    }

    public boolean checkNameAndSurnameFieldValidity(String name, String surname) {
        return !name.isEmpty() && !surname.isEmpty();
    }

    public boolean checkBirthdateValidity(int year, int month, Calendar calendar) {
        boolean check = false;

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        if(year > currentYear - 100 && month > 0 && month < 13) {
            if (year <= currentYear - 14) {
                if (year == currentYear - 14) {
                    if (month <= currentMonth) check = true;
                } else {
                    check = true;
                }
            }
        }

        Log.i(TAG, "Birthday: " + year + " " + month);

        return check;
    }

    public boolean checkWeightAndHeightValidity(Integer weight, Float height) {
        return weight != 0 && height != 0f;
    }


    private void onSaveUser(boolean result) {
        onSaveUser.postValue(result);
    }


    public LiveData<Boolean> getOnSaveUser() {
        return onSaveUser;
    }

}
