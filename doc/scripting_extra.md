- [List Script](#list-script)

## List Script

List all scripts for vault data owner or caller. The caller of the script can check which script can be used.

* **URL**

  `/api/v2/scripting/list`

* **Method:**

  `GET`

* **Header**

  `Authorization: "token 38b8c2c1093dd0fec383a9d9ac940515"`

* **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

    * **Code:** 200 <br />
    * **Content:**

```json
{
  "scripts": [
    {
      "name": "get_messages",
      "executables": {
        "find_messages": {
          "type": "find",
          "params": ["group_id"]
        }
      }
    }
  ]
}
```

`find_messages` is the executable name which contains the type and parameters of the executable.

* **Error Response:**

    * **Code:** `404 NOT FOUND` <br />
      **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

    * **Code:** `401 UNAUTHORIZED` <br />
      **Content:** `{ error : "You are unauthorized to make this request." }`
      