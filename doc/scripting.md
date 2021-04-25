- [Register Script](#register-script)
  - [Condition](#condition)
  - [Executable](#executable)
    - [aggregated](#aggregated)
    - [find](#find)
    - [insert](#insert)
    - [update](#update)
    - [delete](#delete)
    - [fileUpload](#fileupload)
    - [fileDownload](#filedownload)
    - [fileProperties](#fileproperties)
    - [fileHash](#filehash)
  - [Predefined Variable](#predefined-variable)
  - [Parameter](#parameter)
- [Run Script](#run-script)
- [Upload File](#upload-file)
- [Download File](#download-file)
- [Remove Script](#remove-script)

## Register Script

Register a new script for the vault data owner. Script caller will run the script by name later. The script is treated as the channel for other users to access the owner's data. This will set up a condition and an executable. The condition is checked before running the executable. What the executable can do depends on the type of it. For example, the type "find" can query the documents from a collection.

* **URL**

  `/api/v2/vault/scripting/{script_name}`

* **Method:**

  `PUT`

* **Header**

  `Authorization: "token 38b8c2c1093dd0fec383a9d9ac940515"`

* **URL Params**

  None

* **Data Params**

```json
{
  "name": "get_messages",
  "condition": {
    "type": "queryHasResult",
    "name": "verify_user_permission",
    "body": {
      "collection": "groups",
      "filter": {
        "_id": "$params.group_id",
        "friends": "$caller_did"
      }
    }
  },
  "executable": {
    "type": "find",
    "name": "find_messages",
    "output": true,
    "body": {
      "collection": "messages",
      "filter": {
        "group_id": "$params.group_id"
      },
      "options": {"projection": {
          "_id": false
        }, 
        "limit": 100
      }
    }
  }
}
```

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**

```json
{
  "find_messages": {
    "acknowledged":true,
    "matched_count":1,
    "modified_count":1,
    "upserted_id":null
  }
}
```

Key **find_messages** is the executable name.

* **Error Response:**

  * **Code:** `404 NOT FOUND` <br />
    **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

  * **Code:** `401 UNAUTHORIZED` <br />
    **Content:** `{ error : "You are unauthorized to make this request." }`

### Condition

There are three type of conditions: `and`, `or`, `queryHasResult`. the `and` and the `or` are for merging other type conditions and can be recursive. `queryHasResult` is for checking whether the document can be found in the collection by a filter. Here is an example for `and` and `or`:

```json
{
  "condition": {
    "type": "and",
    "name": "verify_user_permission",
    "body": [
      {
        "type": "or",
        "name": "verify_user_permission",
        "body": [
          {
            "type": "queryHasResult",
            "name": "user_in_group",
            "body": {
              "collection": "groups",
              "filter": {
                "_id": "$params.group_id",
                "friends": "$caller_did"
              }
            }
          },
          {
            "type": "queryHasResult",
            "name": "user_in_group",
            "body": {
              "collection": "groups",
              "filter": {
                "_id": "$params.group_id",
                "friends": "$caller_did"
              }
            }
          }
        ]
      },
      {
        "type": "queryHasResult",
        "name": "user_in_group",
        "body": {
          "collection": "groups",
          "filter": {
            "_id": "$params.group_id",
            "friends": "$caller_did"
          }
        }
      }
    ]
  }
}
```

### Executable

There are nine types of executables. Here lists all types with the relating examples. 
For the `request params` and the `response`, please check [Run Script](#run-script) for how to use them.

No response will be provided if `output` option sets to false.

#### aggregated

Conjunct other types of executables to one which can be executed altogether.

- executable

```json
{
  "executable": {
    "type": "aggregated",
    "name": "update_and_delete",
    "body": [
      {
        "type": "update",
        "name": "update_and_return",
        "output": true,
        "body": {
          "collection": "messages",
          "filter": {
            "group_id": "$params.group_id",
            "friend_did": "$caller_did",
            "content": "$params.old_content"
          },
          "update": {
            "$set": {
              "group_id": "$params.group_id",
              "friend_did": "$caller_did",
              "content": "$params.new_content"
            }
          },
          "options": {
            "upsert": true,
            "bypass_document_validation": false
          }
        }
      },
      {
        "type": "delete",
        "name": "delete_and_return",
        "output": true,
        "body": {
          "collection": "messages",
          "filter": {
            "group_id": "$params.group_id",
            "friend_did": "$caller_did",
            "content": "$params.content"
          }
        }
      }
    ]
  }
}
```

- request params

```json
{
  "params": {
    "update_and_return": {
      "group_id": {
        "$oid": "5f8d9dfe2f4c8b7a6f8ec0f1"
      },
      "old_content": "This is the old content",
      "new_content": "This is the new content"
    },
    "delete_and_return": {
      "group_id": {
        "$oid": "5f8d9dfe2f4c8b7a6f8ec0f1"
      },
      "content": "This is the content"
    }
  }
}
```

- response

```json
{
  "update_and_return": {
    "acknowledged": true,
    "matched_count": 1,
    "modified_count": 0,
    "upserted_id": null
  },
  "delete_and_return": {
    "acknowledged": true,
    "deleted_count": 1
  }
}
```

#### find

Query documents from the collection.

- executable

```json
{
  "executable": {
    "type": "find",
    "name": "find_messages",
    "output": true,
    "body": {
      "collection": "messages",
      "filter": {
        "group_id": "$params.group_id"
      },
      "options": {
        "projection": {
          "_id": false
        },
        "limit": 100
      }
    }
  }
}
```

- request params

```json
{
  "params": {
    "group_id": {
      "$oid": "5f8d9dfe2f4c8b7a6f8ec0f1"
    }
  }
}
```

- response

```json
{
  "page_index": 1,
  "page_size": 10,
  "total": 2,
  "items": [
    {
      "author": "john doe1_1",
      "title": "Eve for Dummies1_1",
      "created": {
        "$date": 1630022400000
      },
      "modified": {
        "$date": 1598803861786
      }
    },
    {
      "author": "john doe1_2",
      "title": "Eve for Dummies1_2",
      "created": {
        "$date": 1630022400000
      },
      "modified": {
        "$date": 1598803861786
      }
    }
  ]
}
```

#### insert

Insert a document to the database collection.

- executable

```json
{
  "executable": {
    "type":"insert",
    "name":"database_insert",
    "output":true,
    "body":{
      "collection":"script_database",
      "document":{
        "author":"$params.author",
        "content":"$params.content"
      },
      "options":{
        "ordered":true,
        "bypass_document_validation":false
      }
    }
  }
}
```

- request params

```json
{
  "params": {
    "author": "This is an author.",
    "content": "This is the content."
  }
}
```

- response

```json
{
  "acknowledged": true,
  "matched_count": 1,
  "modified_count": 0,
  "upserted_id": "987345700969670987"
}
```

#### update

Update a document in the database collection.

- executable

```json
{
  "executable": {
    "output":true,
    "name":"database_update",
    "type":"update",
    "body":{
      "collection":"script_database",
      "filter":{
        "author":"$params.old_author"
      },
      "update":{
        "$set":{
          "author":"$params.new_author",
          "content":"$params.content"
        }
      },
      "options":{
        "bypass_document_validation":false,
        "upsert":true
      }
    }
  }
}
```

- request params

```json
{
  "params": {
    "old_author": "This is an old author.",
    "new_author": "This is a new author.",
    "content": "This is the content."
  }
}
```

- response

```json
{
  "acknowledged": true,
  "matched_count": 1,
  "modified_count": 0,
  "upserted_id": null
}
```

#### delete

Delete documents from the database collection by the filter.

- executable

```json
{
  "executable": {
    "output":true,
    "name":"database_delete",
    "type":"delete",
    "body":{
      "collection":"script_database",
      "filter":{
        "author":"$params.author"
      }
    }
  }
}
```

- request params

```json
{
  "params": {
    "author": "This is an author."
  }
}
```

- response

```json
{
  "acknowledged": true,
  "deleted_count": 1
}
```

#### fileUpload

Upload a file to the owner's vault. This will create a new transaction id for the following uploading operation.

- executable

```json
{
  "executable": {
    "output":true,
    "name":"upload_file",
    "type":"fileUpload",
    "body":{
      "path":"$params.path"
    }
  }
}
```

- request params

```json
{
  "params": {
    "path": "this/is/the/file/path"
  }
}
```

- response

```json
{
  "transaction_id": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb3dfaWQiOiI2MDgyN2Q5MWM1N2ExZjQyZmYzZGZmNGUiLCJ0YXJnZXRfZGlkIjoiZGlkOmVsYXN0b3M6aWNYdHBEblpSU0Ryam1ENU5RdDZUWVNwaEZScW9vMnE2biIsInRhcmdldF9hcHBfZGlkIjoiYXBwSWQifQ.b85_gibBglY4OYCoWn3_DUcSxF--XFHZUZYeapQb-zM"
}
```

#### fileDownload

Download a file from the owner's vault. This will also create a new transaction id for the following downloading operation.

- executable

```json
{
  "executable": {
    "output":true,
    "name":"download_file",
    "type":"fileDownload",
    "body":{
      "path":"$params.path"
    }
  }
}
```

- request params

```json
{
  "params": {
    "path": "this/is/the/file/path"
  }
}
```

- response

```json
{
  "transaction_id": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb3dfaWQiOiI2MDgyN2Q5MWM1N2ExZjQyZmYzZGZmNGUiLCJ0YXJnZXRfZGlkIjoiZGlkOmVsYXN0b3M6aWNYdHBEblpSU0Ryam1ENU5RdDZUWVNwaEZScW9vMnE2biIsInRhcmdldF9hcHBfZGlkIjoiYXBwSWQifQ.b85_gibBglY4OYCoWn3_DUcSxF--XFHZUZYeapQb-zM"
}
```

#### fileProperties

Get the properties of the file.

- executable

```json
{
  "executable": {
    "output":true,
    "name":"file_properties",
    "type":"fileProperties",
    "body":{
      "path":"$params.path"
    }
  }
}
```

- request params

```json
{
  "params": {
    "path": "this/is/the/file/path"
  }
}
```

- response

```json
{
  "last_modify":1619164562.8300493,
  "name":"test.txt",
  "size":44,
  "type":"file"
}
```

#### fileHash

Get the hash code of the file content.

- executable

```json
{
  "executable": {
    "output":true,
    "name":"file_hash",
    "type":"fileHash",
    "body":{
      "path":"$params.path"
    }
  }
}
```

- request params

```json
{
  "params": {
    "path": "this/is/the/file/path"
  }
}
```

- response

```json
{
  "SHA256":"4df4e561a755ff82a00b19733d5f9bf29b63753ee57e534b0dce5c34ce086c72"
}
```

### Predefined Variable

Predefined variables can be used for the condition and the executable. The replacement will occur in running stage.

    $caller_did: caller user did
    $caller_app_did: caller application did
    {"$oid": "5f8d9dfe2f4c8b7a6f8ec0f1"}: This represents ObjectId("5f8d9dfe2f4c8b7a6f8ec0f1")

### Parameter

To define parameters for the executable, the following rule must be followed.

    $params.PARAMETER_NAME

For example, `$params.path` defines a new parameter `path`. As running a script, we just need provide the value of the parameter `path` like this:

    path: "REAL_FILE_PATH"

`$params.path` will be replaced by value `"REAL_FILE_PATH"`.
  
## Run Script

Run the script registered by the owner. Before running the script, the caller need check if he matches the script condition.

* **URL**

  `/api/v2/vault/scripting/{script_name}`

* **Method:**

  `PATCH`

* **Header**

  `Authorization: "token 38b8c2c1093dd0fec383a9d9ac940515"`

* **URL Params**

  None

* **Data Params**

```json
{
  "context": {
    "target_did": "did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n",
    "target_app_did":"appId"
  },
  "params": {
    "group_id": {"$oid": "5f8d9dfe2f4c8b7a6f8ec0f1"}
  }
}
```

`find_messages` is the executable name. The values of `find_messages` are parameters and their values.

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**

```json
{
  "page_index": 1,
  "page_size": 10,
  "total": 2,
  "items": [
    {
      "author": "john doe1_1",
      "title": "Eve for Dummies1_1",
      "created": {
        "$date": 1630022400000
      },
      "modified": {
        "$date": 1598803861786
      }
    },
    {
      "author": "john doe1_2",
      "title": "Eve for Dummies1_2",
      "created": {
        "$date": 1630022400000
      },
      "modified": {
        "$date": 1598803861786
      }
    }
  ]
}
```

* **Error Response:**

  * **Code:** `404 NOT FOUND` <br />
    **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

  * **Code:** `401 UNAUTHORIZED` <br />
    **Content:** `{ error : "You are unauthorized to make this request." }`

## Upload File

Upload file by transaction id returned by running script for the executable type `fileUpload`.

* **URL**

  `/api/v2/vault/scripting/streaming/{transaction_id}`

* **Method:**

  `POST`

* **Header**

  `Authorization: "token 38b8c2c1093dd0fec383a9d9ac940515"`

* **URL Params**

  None

* **Data Params**

  The file content.

* **Success Response:**

  * **Code:** 201 <br />
  * **Content:**

  None

* **Error Response:**

  * **Code:** `404 NOT FOUND` <br />
    **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

  * **Code:** `401 UNAUTHORIZED` <br />
    **Content:** `{ error : "You are unauthorized to make this request." }`

## Download File

Download file by transaction id returned by running script for the executable type `fileDownload`.

* **URL**

  `/api/v2/vault/scripting/streaming/{transaction_id}`

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

  File content.

* **Error Response:**

  * **Code:** `404 NOT FOUND` <br />
    **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

  * **Code:** `401 UNAUTHORIZED` <br />
    **Content:** `{ error : "You are unauthorized to make this request." }`

## Remove Script

Download file by transaction id returned by running script for the executable type `fileDownload`.

* **URL**

  `/api/v2/vault/scripting/{script_name}`

* **Method:**

  `DELETE`

* **Header**

  `Authorization: "token 38b8c2c1093dd0fec383a9d9ac940515"`

* **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 204 <br />
  * **Content:**

  File content.

* **Error Response:**

  * **Code:** `404 NOT FOUND` <br />
    **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

  * **Code:** `401 UNAUTHORIZED` <br />
    **Content:** `{ error : "You are unauthorized to make this request." }`
