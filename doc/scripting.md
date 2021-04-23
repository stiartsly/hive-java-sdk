
[List Script](#list-script)<br>
[Register Script](#register-script)<br>
[Run Script](#run-script)<br>
[Upload File](#upload-file)<br>
[Download File](#download-file)<br>
[Remove Script](#remove-script)<br>


## List Script

## Register Script

----

Register a new script for the vault data owner. Script caller will run the script by name later. The script is treated as the channel for other users to access the owner's data. This will set up a condition and an executable. The condition is checked before running the executable. What the executable can do depends on the type of it. For example, the type "find" can query the documents from a collection.

* **URL**

  `/api/v2/scripting/set_script/{script_name}`

* **Method:**

  `POST`

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

* **Condition**

There are three type of conditions: **and**, **or**, **queryHasResult**. **and** and **or** are for merging other type conditions and can be recursive. **queryHasResult** is for checking whether the document can be found in the collection by a filter. Here is an example for **and** and **or**:

```json
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
```

* **Executable:**

There are nine types of executables. Here lists all types with the relating examples.

1. aggregated: Conjunct other types of executables to one which can be executed altogether.

```json
"executable": {
  "type": "aggregated",
  "name": "update_and_delete",
  "body": [
    {
      "type": "update",
      "name": "update_and_return",
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
```

2. find: query documents from collection.

```json
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
```

3. insert: insert a document to database collection

```json
"executable": {
  "output":true,
  "name":"database_insert",
  "type":"insert",
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
```

4. update: update a document in database collection

```json
"executable": {
  "output":true,
  "name":"database_update",
  "type":"update",
  "body":{
    "update":{
      "$set":{
        "author":"$params.author",
        "content":"$params.content"
      }
    },
    "options":{
      "bypass_document_validation":false,
      "upsert":true
    },
    "collection":"script_database",
    "filter":{
      "author":"$params.author"
    }
  }
}
```

5. delete: delete documents from database collection by filter

```json
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
```

6. fileUpload: Upload a file to the owner's vault. This will create a new transaction id for the following uploading operation.

```json
"executable": {
  "output":true,
  "name":"upload_file",
  "type":"fileUpload",
  "body":{
    "path":"$params.path"
  }
}
```

7. fileDownload: Download a file from the owner's vault. This will also create a new transaction id for the following downloading operation.

```json
"executable": {
  "output":true,
  "name":"download_file",
  "type":"fileDownload",
  "body":{
    "path":"$params.path"
  }
}
```

8. fileProperties: Get the properties of the file.

```json
"executable": {
  "output":true,
  "name":"file_properties",
  "type":"fileProperties",
  "body":{
    "path":"$params.path"
  }
}
```

9. fileHash: Get the hash code of the file content.


```json
"executable": {
  "output":true,
  "name":"file_hash",
  "type":"fileHash",
  "body":{
    "path":"$params.path"
  }
}
```

* **Error Response:**

  * **Code:** `404 NOT FOUND` <br />
    **Content:** `{ error : "Vault not found or not activate for the script." }`

  OR

  * **Code:** `401 UNAUTHORIZED` <br />
    **Content:** `{ error : "You are unauthorized to make this request." }`
  
## Run Script

## Upload File

## Download File

## Remove Script
