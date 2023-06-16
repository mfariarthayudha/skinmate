const functions = require("firebase-functions")
const { onDocumentUpdated } = require("firebase-functions/v2/firestore")
const { initializeApp } = require("firebase-admin/app")
const { getAuth } = require("firebase-admin/auth")
const { getFirestore } = require("firebase-admin/firestore")
const { getStorage } = require("firebase-admin/storage")

const application = initializeApp({
	storageBucket: "gs://skinmate-e2e32.appspot.com/",
})
const defaultProfilePictureURL = "https://firebasestorage.googleapis.com/v0/b/skinmate-e2e32.appspot.com/o/profile-picture%2Fdefault-profile-picture.png?alt=media&token=09ae5e4f-dc40-41f6-9ae7-60d37aae28echttp://127.0.0.1:9199/v0/b/skinmate-e2e32.appspot.com/o/profile-picture%2Fdefault-profile-picture.png?alt=media&token=e191f35d-d123-4636-ac58-0223566f0687"

exports.setUserDefaultProfilePicture = functions.auth.user().onCreate(async (user) => {
	try {
		const authentication = getAuth(application)
		const firestore = getFirestore(application)

		await authentication.updateUser(user.uid, {
			photoURL: defaultProfilePictureURL,
		})

		await firestore.collection("users").doc(user.uid).set({ photoURL: defaultProfilePictureURL })
	} catch (error) {
		return error
	}
})

exports.handleUpdateProfilePicture = onDocumentUpdated("users/{userUID}", async (event) => {
	try {
		const authentication = getAuth(application)
		const storage = getStorage(application).bucket()
		const previousPhotoURL = event.data.before.data()["photoURL"]
		const newPhotoURL = event.data.after.data()["photoURL"]

		if (previousPhotoURL == newPhotoURL) return false

		await event.data.after.ref.set({ photoURL: newPhotoURL })

		await authentication.updateUser(event.data.after.id, {
			photoURL: newPhotoURL,
		})

		if (previousPhotoURL != defaultProfilePictureURL) {
			await storage.deleteFiles({ prefix: `profile-picture/${previousPhotoURL.substring(previousPhotoURL.indexOf("%2F") + 3, previousPhotoURL.indexOf("?"))}` })
		}

		return true
	} catch (error) {
		return error
	}
})
