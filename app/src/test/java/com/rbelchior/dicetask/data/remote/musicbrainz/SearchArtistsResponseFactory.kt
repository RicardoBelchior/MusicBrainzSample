package com.rbelchior.dicetask.data.remote.musicbrainz

object SearchArtistsResponseFactory {

    val sampleResponse = """
{
    "created": "2023-02-10T02:04:51.437Z",
    "count": 11,
    "offset": 0,
    "artists":
    [
        {
            "id": "cc197bad-dc9c-440d-a5b5-d52ba2e14234",
            "type": "Group",
            "type-id": "e431f5f6-b5d2-343d-8b36-72607fffb74b",
            "score": 100,
            "name": "Coldplay",
            "sort-name": "Coldplay",
            "country": "GB",
            "area":
            {
                "id": "8a754a16-0027-3a29-b6d7-2b40ea0481ed",
                "type": "Country",
                "type-id": "06dd0ae4-8c74-30bb-b43d-95dcedf961de",
                "name": "United Kingdom",
                "sort-name": "United Kingdom",
                "life-span":
                {
                    "ended": null
                }
            },
            "begin-area":
            {
                "id": "f03d09b3-39dc-4083-afd6-159e3f0d462f",
                "type": "City",
                "type-id": "6fd8f29a-3d0a-32fc-980d-ea697b69da78",
                "name": "London",
                "sort-name": "London",
                "life-span":
                {
                    "ended": null
                }
            },
            "isnis":
            [
                "000000011551394X"
            ],
            "life-span":
            {
                "begin": "1996-09",
                "ended": null
            },
            "aliases":
            [
                {
                    "sort-name": "コールドプレイ",
                    "type-id": "894afba6-2816-3c24-8072-eadb66bd04bc",
                    "name": "コールドプレイ",
                    "locale": "ja",
                    "type": "Artist name",
                    "primary": null,
                    "begin-date": null,
                    "end-date": null
                },
                {
                    "sort-name": "Cold Play",
                    "type-id": "1937e404-b981-3cb7-8151-4c86ebfc8d8e",
                    "name": "Cold Play",
                    "locale": null,
                    "type": "Search hint",
                    "primary": null,
                    "begin-date": null,
                    "end-date": null
                },
                {
                    "sort-name": "Coldplay, The",
                    "type-id": "894afba6-2816-3c24-8072-eadb66bd04bc",
                    "name": "The Coldplay",
                    "locale": null,
                    "type": "Artist name",
                    "primary": null,
                    "begin-date": null,
                    "end-date": "1998"
                }
            ],
            "tags":
            [
                {
                    "count": 5,
                    "name": "rock"
                },
                {
                    "count": 11,
                    "name": "pop"
                }
            ]
        },
        {
            "id": "62c54a75-265f-4e13-ad0a-0fb001559a2e",
            "type": "Group",
            "type-id": "e431f5f6-b5d2-343d-8b36-72607fffb74b",
            "score": 62,
            "name": "Viva La Coldplay",
            "sort-name": "Viva La Coldplay",
            "country": "GB",
            "area":
            {
                "id": "8a754a16-0027-3a29-b6d7-2b40ea0481ed",
                "type": "Country",
                "type-id": "06dd0ae4-8c74-30bb-b43d-95dcedf961de",
                "name": "United Kingdom",
                "sort-name": "United Kingdom",
                "life-span":
                {
                    "ended": null
                }
            },
            "life-span":
            {
                "begin": "2006",
                "ended": null
            }
        },
        {
            "id": "a7331511-edcf-4e1f-b2b4-46f3b30f7f32",
            "type": "Person",
            "type-id": "b6e035f4-3ce9-331c-97df-83397230b0df",
            "score": 62,
            "name": "冷玩妹",
            "sort-name": "Coldplay Sister",
            "country": "TW",
            "area":
            {
                "id": "41637cec-9a4f-389c-86d2-fc6abf3357b5",
                "type": "Country",
                "type-id": "06dd0ae4-8c74-30bb-b43d-95dcedf961de",
                "name": "Taiwan",
                "sort-name": "Taiwan",
                "life-span":
                {
                    "ended": null
                }
            },
            "life-span":
            {
                "ended": null
            },
            "aliases":
            [
                {
                    "sort-name": "Coldplay Sister",
                    "name": "Coldplay Sister",
                    "locale": "en",
                    "type": null,
                    "primary": true,
                    "begin-date": null,
                    "end-date": null
                }
            ]
        }
    ]
}
""".trimIndent()

}