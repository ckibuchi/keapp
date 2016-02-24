package in.co.madhur.chatbubblesdemo.utils;



//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.util.Log;
import android.view.View;
        import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
        import android.widget.VideoView;

/**
 * Created by rube on 10/10/15.
 */
public class UploadHelper {

        private static final String TAG = UploadHelper.class.getSimpleName();

        private ProgressBar progressBar;
        private String filePath = null;;
        long totalSize = 0;
        TextView txtPercentage = null;

        public UploadHelper(ProgressBar progressBar, String filePath){
            this.progressBar = progressBar;
            this.filePath = filePath;
        }

        public UploadHelper(ProgressBar progressBar, String filePath, TextView txtPercentage){
            this.progressBar = progressBar;
            this.filePath = filePath;
            this.txtPercentage = txtPercentage;
        }

        public UploadHelper(String filePath){
            this.filePath = filePath;
        }

        /**
         * Displaying captured image/video on the screen
         * */
        private void previewImage(ImageView imgPreview) {
            // Checking whether captured media is image or video

            imgPreview.setVisibility(View.VISIBLE);
            //vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        }

        public  void previewVideo(VideoView vidPreview){

                vidPreview.setVisibility(View.VISIBLE);
                vidPreview.setVideoPath(filePath);
                // start playing
                vidPreview.start();

        }

        /**
         * Uploading the file to server
         * */
        private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
            @Override
            protected void onPreExecute() {
                // setting progress bar to zero
                if(progressBar != null) {
                    progressBar.setProgress(0);
                }
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                if(progressBar != null) {
                    // Making progress bar visible
                    progressBar.setVisibility(View.VISIBLE);

                    // updating progress bar value
                    progressBar.setProgress(progress[0]);

                    // updating percentage value
                    txtPercentage.setText(String.valueOf(progress[0]) + "%");
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                return uploadFile();
            }

            @SuppressWarnings("deprecation")
            private String uploadFile() {
                String responseString = null;

               /** HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.SERVER_URL+"/uploads/");

                try {
                    UploadMultiPartHelper entity = new UploadMultiPartHelper(
                            new UploadMultiPartHelper.ProgressListener() {

                                @Override
                                public void transferred(long num) {
                                    publishProgress((int) ((num / (float) totalSize) * 100));
                                }
                            });

                    File sourceFile = new File(filePath);

                    // Adding file data to http body
                    entity.addPart("image", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("website",
                            new StringBody("www.keapp.com"));

                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }

                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                } catch (IOException e) {
                    responseString = e.toString();
                }
                **/
                return responseString;


            }

            @Override
            protected void onPostExecute(String result) {
                Log.e(TAG, "Response from server: " + result);
               super.onPostExecute(result);
            }

        }



}
