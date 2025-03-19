import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
public class FireBase {
    public static FirebaseAuth mAuth;

    public static FirebaseAuth getAuth(){
        if(mAuth == null)
            mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    public static void CreateUser (String email, String password, Context context)
    {


        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context.getApplicationContext(),"Authentication Successful",Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context.getApplicationContext(), mainActivity.class));

                        } else {
                            Toast.makeText(context.getApplicationContext(),"Authentication failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
