# Public Subnet Creation
resource "aws_subnet" "public-subnet" {
  vpc_id = aws_vpc.cicd-vpc.id
  availability_zone = "ap-south-1a"
  cidr_block = "180.198.16.0/20"
  map_public_ip_on_launch = "true"
  tags = {
    Name = "public-subnet"
  }
}

# Private Subnet Creation
resource "aws_subnet" "private-subnet" {
  vpc_id = aws_vpc.cicd-vpc.id
  cidr_block = "180.198.32.0/20"
  availability_zone = "ap-south-1a"
  tags = {
    Name = "private-subnet"
  }
}
