package com.example.espanholgenialstorageandroid.strategy

class SanitizeFileNameStrategy : SanitizeFileNameInterface
{
    override fun sanitizeFileName(input: String): String
    {
       val regex = Regex("[#\\[\\]*?\"<>|%\\\\{}^~:/ ]")

        if(regex.containsMatchIn(input))
        {
            throw IllegalArgumentException("O nome contém caracteres proibidos: # [ ] * ? \" < > | % \\ { } ^ ~ : / espaço")
        }

        return input
    }
}