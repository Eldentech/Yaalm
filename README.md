# YAALM (Yet Another Android Location Manager)

Yaalm is a library aimed to simplify android location permission management and location data achivement using LiveData

Basic usage:

Implement Yaalm initialization inside application:

```kotlin
    class App: Application() {
        override fun onCreate() {
            super.onCreate()
            Yaalm.configure(this)
        }
    }

```

Implement overrides inside your fragment or activity and start observing:

```kotlin
class LocationDataFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Yaalm.instance.locationLiveData.observe(this, Observer<LocationData> {
            status.text = it?.locationDataStatus?.name
            if(it!= null){
                val locationString = """
                    Latitude: ${it.location?.latitude}
                    Longitude: ${it.location?.longitude}
                    Altitude: ${it.location?.altitude}
                    """.trimIndent()
                location.text = locationString
            }

        })

    }

    override fun onResume() {
        super.onResume()
        Yaalm.instance.setActiveActivity(this.activity as Activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Yaalm.instance.onActivityResult(requestCode,resultCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Yaalm.instance.onRequestPermissionsResult(requestCode,grantResults)
    }

}

````
