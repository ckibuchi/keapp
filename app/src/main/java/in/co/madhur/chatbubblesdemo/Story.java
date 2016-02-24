package in.co.madhur.chatbubblesdemo;

//import org.lucasr.twowayview.TwoWayView;

/**
 * Created by rube on 07/11/15.
 */
public class Story {/*extends ActionBarActivity {

    private Toolbar toolbar;
    private String pageId;
    private String thisPageName;
    private DBProvider dbProvider;
    private TextView saveStoryButton;
    private EditText storyNameTextView;
    private EditText storyTextTextView;
    private ImageView uploadStroyImageButton;

    TwoWayView imageHorizontalList;
    private ArrayList<Bitmap> storyImages = new ArrayList<Bitmap>();

    private final String TAG = Story.class.getSimpleName();
    private final int PICK_IMAGE_REQUEST = 1;
    private final int VIEW_PAGE_FILES_PHOTO =2;
    private static ImageAdapter imageAdapter;
    private static boolean storyAlreadySaved = false;
    private static String thisStoryId = "";
    private String loggedInUserID  = "0";

    public static final String preference = "MyAccountPreference" ;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        Bundle extras = getIntent().getExtras();
        pageId = extras.getString("pageId");
        thisPageName = extras.getString("pageName");

        toolbar = (Toolbar) findViewById(R.id.page_tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(thisPageName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        saveStoryButton = (TextView)findViewById(R.id.save_story);
        dbProvider = new DBProvider(this);
        storyNameTextView = (EditText)findViewById(R.id.story_name_text);
        storyTextTextView = (EditText)findViewById(R.id.new_story_text);
        uploadStroyImageButton = (ImageView)findViewById(R.id.story_upload_image);
        imageHorizontalList = (TwoWayView)findViewById(R.id.horizontalList);
        saveStoryButton.setOnClickListener(saveStoryButtonClickListener);

        uploadStroyImageButton.setOnClickListener(uploadStoryImageClickListener);


    }

    /********* Called when Item click in ListView ************
    public View.OnClickListener uploadStoryImageClickListener = new   View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            //Do image upload here
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            //fileUri =  Utils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            //Utils.captureImage(Page.this, fileUri);
        }
    };

    public View.OnClickListener saveStoryButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!storyAlreadySaved) {
                saveStory();
            }
        }
    };


    public void saveStory(){

        String storyName = storyNameTextView.getText().toString();

        if(storyName == null ){
            Utils.createToast(this, "Invalid story name", Toast.LENGTH_SHORT);
            //pageNameEditText.setText("");
            return;
        }
        if(storyName.equals("") ){
            Utils.createToast(this, "Invalid story name", Toast.LENGTH_SHORT);
            //pageNameEditText.setText("");
            return;
        }

        Cursor pageCheckCursor = dbProvider.query(
                DB.Story.CONTENT_URI, new String[]{DB.Story._ID},
                DB.Story.NAME + "=? and " + DB.Story.PAGE_ID + "=?", new String[]{storyName, pageId}, null);

        if(pageCheckCursor.getCount() > 0){
            Utils.createToast(this, "Duplicate story name for page "+thisPageName, Toast.LENGTH_SHORT);
            //pageNameEditText.setText("");
            return;
        }


        if(pageCheckCursor.getCount() > 0){
            Utils.createToast(this, "Duplicate story name for page "+thisPageName, Toast.LENGTH_SHORT);
            return;
        }

        String storyText  = storyTextTextView.getText().toString();
        if(storyText.equals("")){
            Utils.createToast(this, "Empty story text", Toast.LENGTH_SHORT);
            return;
        }

        sharedPreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("accountName", "");
        if(userName.equals("")){
            Utils.createToast(this, "Invalid account login, please re-login to proceed", Toast.LENGTH_SHORT);
            return;
        }

        Cursor profileIdCursor = dbProvider.query(DB.Profile.CONTENT_URI, new String[]{DB.Profile.USERNAME, DB.Profile._ID},
                DB.Profile.USERNAME+" =?", new String[]{userName}, null);


        try {
            if (profileIdCursor.moveToFirst()) {
                loggedInUserID = profileIdCursor.getString(profileIdCursor.getColumnIndex(DB.Profile._ID));
            }
            profileIdCursor.close();
        }catch (Exception ex){
            Log.d(TAG, "Error attempting to get loggedInUserID: "+ex.getMessage());
            Utils.createToast(this, "Invalid account login, please re-login to proceed", Toast.LENGTH_SHORT);
            return;
        }


        ContentValues params = new ContentValues();
        params.put(DB.Story.NAME, storyName);
        params.put(DB.Story.PROFILE_ID, loggedInUserID);
        params.put(DB.Story.PAGE_ID, pageId);
        params.put(DB.Story.TEXT, storyText );


        Uri uri = dbProvider.insert(DB.Story.CONTENT_URI, params);
        thisStoryId = uri.getLastPathSegment();
        storyAlreadySaved = true;
        saveStoryMediaFiles(thisStoryId);
        Utils.createToast(this, "Story save success", Toast.LENGTH_SHORT);
        saveStoryButton.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveStoryMediaFiles(String storyId){
        Log.d(TAG, "Calling save page ID");
        int count = 1;

        for(Bitmap bmp: storyImages){
            saveStoryFilesImage(bmp, pageId, count);
            count++;

        }
        Log.d(TAG, "Create image pass returning");
    }

    public  void saveStoryFilesImage(Bitmap bmp, String storyId, int count){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String date = df.format(new Date());
        df = new SimpleDateFormat("yyyyMMddhhss");
        String date_ext = df.format(new Date());

        Log.d(TAG, "Found profile images " + storyImages.size());
        String fileName = "PAGE_"+date_ext+ "_"+count;

        Log.d(TAG, "Saving file page image : " + fileName);
        Utils.saveImage(this, bmp, fileName, "jpg");

        ContentValues args = new ContentValues();
        args.put(DB.Media.SERVER_ID, 0);
        args.put(DB.Media.MEDIA_TYPE, "PHOTO");
        args.put(DB.Media.CONTENT_ID, storyId);
        args.put(DB.MediaColumns.CONTENT_TYPE, "STORY");
        args.put(DB.Media.URL, fileName);
        args.put(DB.Media.DESCRIPTION, "Story media");
        args.put(DB.Media.DATE_CREATED, date);

        Uri uri = dbProvider.insert(DB.Media.CONTENT_URI, args);
        String mediaId = uri.getLastPathSegment();
        Log.d(TAG, "Image  inserted success ID " + mediaId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.d(TAG, "Loading PICK_IMAGE_REQUEST after result:" + uri.toString());

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.inScaled = false;

                AssetFileDescriptor fileDescriptor = null;
                fileDescriptor =
                        getContentResolver().openAssetFileDescriptor(uri, "r");

                Bitmap actuallyUsableBitmap
                        = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                storyImages.add(actuallyUsableBitmap);
                //Do image save before share here

                imageAdapter = new ImageAdapter(this, storyImages);
                imageHorizontalList.setAdapter(imageAdapter);
                imageHorizontalList.setItemMargin(2);
                imageHorizontalList.setOnItemClickListener(storyFilesClickLister);
                //Attempt to save page on imageUpload
                if (!storyAlreadySaved) {
                    saveStory();
                } else {
                    saveStoryFilesImage(actuallyUsableBitmap, thisStoryId, storyImages.size());
                }
                Log.d(TAG, "Image loaded successfully");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public AdapterView.OnItemClickListener storyFilesClickLister = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            //We could have missed saving page save it now
            Log.d(TAG, "Found on item click position " + position);
            if(!storyAlreadySaved) {
                saveStory();
                if (!storyAlreadySaved) {
                    return;
                }
            }

            Bitmap image = (Bitmap)imageAdapter.getItem(position);
            Intent intent = new Intent(Story.this, BigImageViewActivity.class);
            Bundle extras = new Bundle();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            String storyName = storyNameTextView.getText().toString();

            image.compress(Bitmap.CompressFormat.PNG, 100, bs);
            extras.putByteArray("image", bs.toByteArray());
            extras.putString("title", storyName + " Media " + position + 1 + " of " + storyImages.size());
            extras.putString("position", ""+position);
            extras.putString("fileName", "");
            extras.putBoolean("showEdit", false);
            intent.putExtras(extras);
            Log.d(TAG, "Calling start big image load");
            Story.this.startActivityForResult(intent, VIEW_PAGE_FILES_PHOTO);

        }
    };
*/
}
